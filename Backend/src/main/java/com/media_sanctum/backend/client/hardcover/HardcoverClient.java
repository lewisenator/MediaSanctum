package com.media_sanctum.backend.client.hardcover;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverException;
import com.media_sanctum.backend.client.hardcover.model.HardcoverAuthor;
import com.media_sanctum.backend.client.hardcover.model.HardcoverAuthorSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverBook;
import com.media_sanctum.backend.client.hardcover.model.HardcoverBookSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverSearchResult;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
public class HardcoverClient {

    static final String INSTANCE = "hardcover";

    private static final String ERRORS_FIELD = "errors";



    private final String apiKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public HardcoverClient(
            String apiKey,
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @RateLimiter(name = INSTANCE)
    public HardcoverSearchResult<HardcoverAuthorSearchResult> searchAuthors(String query) {
        JsonNode root = executeQuery(HardcoverQueries.SEARCH_AUTHORS, Map.of("q", safe(query)));
        JsonNode searchResults = root.path("data").path("search").path("results");
        var targetType = objectMapper.getTypeFactory()
                .constructParametricType(HardcoverSearchResult.class, HardcoverAuthorSearchResult.class);
        return objectMapper.convertValue(searchResults, targetType);
    }

    @RateLimiter(name = INSTANCE)
    public HardcoverSearchResult<HardcoverBookSearchResult> searchBooks(String query) {
        JsonNode root = executeQuery(HardcoverQueries.SEARCH_BOOKS, Map.of("q", safe(query)));
        JsonNode searchResults = root.path("data").path("search").path("results");
        var targetType = objectMapper.getTypeFactory()
                .constructParametricType(HardcoverSearchResult.class, HardcoverBookSearchResult.class);
        return objectMapper.convertValue(searchResults, targetType);
    }

    @RateLimiter(name = INSTANCE)
    public HardcoverBook getBook(Integer hardcoverBookId) {
        JsonNode root = executeQuery(HardcoverQueries.GET_BOOK, Map.of("q", hardcoverBookId));
        JsonNode searchResults = root.path("data").path("books_by_pk");
        return objectMapper.convertValue(searchResults, HardcoverBook.class);
    }

    @RateLimiter(name = INSTANCE)
    public HardcoverAuthor getAuthor(Integer hardcoverAuthorId) {
        JsonNode root = executeQuery(HardcoverQueries.GET_AUTHOR, Map.of("q", hardcoverAuthorId));
        JsonNode searchResults = root.path("data").path("authors_by_pk");
        return objectMapper.convertValue(searchResults, HardcoverAuthor.class);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
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
        } catch (HardcoverException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while executing query {}: {}", query, e.getMessage(), e);
            throw new HardcoverException("Error while executing query " + query, e);
        }
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
