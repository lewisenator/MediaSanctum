package com.media_sanctum.backend.client.hardcover;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverClientException;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverException;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverRateLimitException;
import com.media_sanctum.backend.client.hardcover.model.HardcoverAuthor;
import com.media_sanctum.backend.client.hardcover.model.HardcoverAuthorSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverBook;
import com.media_sanctum.backend.client.hardcover.model.HardcoverBookSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverSearchResult;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
public class HardcoverClient {

    public static final String ERRORS_FIELD = "errors";
    public static final String DATA = "data";
    public static final String SEARCH = "search";
    public static final String RESULTS = "results";

    private final String apiKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String endpoint;
    private final RateLimiter hardcoverRateLimiter;
    private final Retry hardcoverRateLimitRetry;
    private final Retry hardcoverGeneralRetry;

    public HardcoverClient(
            String apiKey,
            RestClient restClient,
            ObjectMapper objectMapper,
            String endpoint,
            RateLimiter hardcoverRateLimiter,
            Retry hardcoverRateLimitRetry,
            Retry hardcoverGeneralRetry
    ) {
        this.apiKey = apiKey;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.endpoint = endpoint;
        this.hardcoverRateLimiter = hardcoverRateLimiter;
        this.hardcoverRateLimitRetry = hardcoverRateLimitRetry;
        this.hardcoverGeneralRetry = hardcoverGeneralRetry;
    }

    public HardcoverSearchResult<HardcoverAuthorSearchResult> searchAuthors(String query) {
        JsonNode root = executeWithResilience(HardcoverQueries.SEARCH_AUTHORS, Map.of("q", safe(query)));
        JsonNode searchResults = root.path(DATA).path(SEARCH).path(RESULTS);
        var targetType = objectMapper.getTypeFactory()
                .constructParametricType(HardcoverSearchResult.class, HardcoverAuthorSearchResult.class);
        return objectMapper.convertValue(searchResults, targetType);
    }

    public HardcoverSearchResult<HardcoverBookSearchResult> searchBooks(String query) {
        JsonNode root = executeWithResilience(HardcoverQueries.SEARCH_BOOKS, Map.of("q", safe(query)));
        JsonNode searchResults = root.path(DATA).path(SEARCH).path(RESULTS);
        var targetType = objectMapper.getTypeFactory()
                .constructParametricType(HardcoverSearchResult.class, HardcoverBookSearchResult.class);
        return objectMapper.convertValue(searchResults, targetType);
    }

    public HardcoverBook getBook(Integer hardcoverBookId) {
        JsonNode root = executeWithResilience(HardcoverQueries.GET_BOOK, Map.of("q", hardcoverBookId));
        JsonNode searchResults = root.path(DATA).path("books_by_pk");
        return objectMapper.convertValue(searchResults, HardcoverBook.class);
    }

    public HardcoverAuthor getAuthor(Integer hardcoverAuthorId) {
        JsonNode root = executeWithResilience(HardcoverQueries.GET_AUTHOR, Map.of("q", hardcoverAuthorId));
        JsonNode searchResults = root.path(DATA).path("authors_by_pk");
        return objectMapper.convertValue(searchResults, HardcoverAuthor.class);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Wraps a single query execution with the local proactive rate limiter and the two reactive
     * retry instances, then enriches whichever exception survives exhaustion with diagnostics
     * (endpoint, query, attempt count, elapsed time, captured rate-limit headers) so
     * {@code GlobalExceptionHandler} can log them without needing to know about resilience4j.
     */
    private JsonNode executeWithResilience(String query, Map<String, Object> variables) {
        Instant start = Instant.now();
        AtomicInteger attempts = new AtomicInteger(0);

        Supplier<JsonNode> counted = () -> {
            attempts.incrementAndGet();
            return executeQuery(query, variables);
        };

        Supplier<JsonNode> throttled = () -> {
            try {
                return RateLimiter.decorateSupplier(hardcoverRateLimiter, counted).get();
            } catch (RequestNotPermitted e) {
                throw new HardcoverRateLimitException(
                        "Hardcover local rate limiter rejected request for query " + query, null);
            }
        };
        Supplier<JsonNode> generalRetried = Retry.decorateSupplier(hardcoverGeneralRetry, throttled);
        Supplier<JsonNode> rateLimitRetried = Retry.decorateSupplier(hardcoverRateLimitRetry, generalRetried);

        try {
            return rateLimitRetried.get();
        } catch (HardcoverRateLimitException e) {
            throw (HardcoverRateLimitException) e.withDiagnostics(
                    endpoint, query, attempts.get(), Duration.between(start, Instant.now()), e.getRateLimitHeaders());
        } catch (HardcoverClientException e) {
            throw e;
        } catch (HardcoverException e) {
            throw e.withDiagnostics(
                    endpoint, query, attempts.get(), Duration.between(start, Instant.now()), e.getRateLimitHeaders());
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private JsonNode executeQuery(String query, Map<String, Object> variables) {
        var safeVars = variables != null ? variables : Map.<String, Object>of();
        var body = Map.of("query", query, "variables", safeVars);
        try {
            String responseBody = restClient
                    .post()
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            return parseResponse(responseBody);
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw toRateLimitException(query, e);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new HardcoverClientException(
                    "Hardcover rejected query " + query + " with 403 (likely batch-limit exceeded): "
                            + e.getMessage(), 403);
        } catch (HttpClientErrorException e) {
            throw new HardcoverClientException(
                    "Hardcover client error " + e.getStatusCode().value() + " for query " + query + ": "
                            + e.getMessage(), e.getStatusCode().value());
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new HardcoverException("Hardcover transient error while executing query " + query, e);
        } catch (HardcoverException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while executing query {}: {}", query, e.getMessage(), e);
            throw new HardcoverException("Error while executing query " + query, e);
        }
    }

    private HardcoverRateLimitException toRateLimitException(String query, HttpClientErrorException.TooManyRequests e) {
        HttpHeaders headers = e.getResponseHeaders();
        Duration retryAfter = parseRetryAfter(headers);
        Map<String, String> rateLimitHeaders = captureRateLimitHeaders(headers);
        var ex = new HardcoverRateLimitException(
                "Hardcover rate limit (429) for query " + query, retryAfter);
        ex.withDiagnostics(null, null, 0, null, rateLimitHeaders);
        return ex;
    }

    private Duration parseRetryAfter(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        String retryAfter = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (retryAfter != null) {
            Duration fromSeconds = parseRetryAfterSeconds(retryAfter);
            if (fromSeconds != null) {
                return fromSeconds;
            }
            Duration fromDate = parseRetryAfterHttpDate(retryAfter);
            if (fromDate != null) {
                return fromDate;
            }
        }
        return parseEpochSecondsHeader(headers.getFirst("X-RateLimit-Reset"));
    }

    private Duration parseRetryAfterSeconds(String value) {
        try {
            return Duration.ofSeconds(Long.parseLong(value.trim()));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private Duration parseRetryAfterHttpDate(String value) {
        try {
            ZonedDateTime resetAt = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
            Duration duration = Duration.between(ZonedDateTime.now(resetAt.getZone()), resetAt);
            return duration.isNegative() ? Duration.ZERO : duration;
        } catch (DateTimeParseException dtpe) {
            return null;
        }
    }

    private Duration parseEpochSecondsHeader(String value) {
        if (value == null) {
            return null;
        }
        try {
            long epochSeconds = Long.parseLong(value.trim());
            Duration duration = Duration.between(Instant.now(), Instant.ofEpochSecond(epochSeconds));
            return duration.isNegative() ? Duration.ZERO : duration;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private Map<String, String> captureRateLimitHeaders(HttpHeaders headers) {
        if (headers == null) {
            return Map.of();
        }
        Map<String, String> captured = new HashMap<>();
        headers.forEach((name, values) -> {
            if (!values.isEmpty()
                    && (name.equalsIgnoreCase("RateLimit")
                    || name.regionMatches(true, 0, "X-RateLimit", 0, "X-RateLimit".length())
                    || name.equalsIgnoreCase(HttpHeaders.RETRY_AFTER))) {
                captured.put(name, values.get(0));
            }
        });
        return captured;
    }

    private JsonNode parseResponse(String responseBody) throws JsonProcessingException {
        JsonNode response = objectMapper.readTree(responseBody);
        if (response == null) {
            throw new HardcoverException("Empty response from hardcover");
        }
        JsonNode errorsNode = response.get(ERRORS_FIELD);
        if (errorsNode != null && errorsNode.isArray() && !errorsNode.isEmpty()) {
            var errors = objectMapper.writeValueAsString(errorsNode);
            throw new HardcoverException("Error response from hardcover: " + errors);
        }
        return response;
    }
}
