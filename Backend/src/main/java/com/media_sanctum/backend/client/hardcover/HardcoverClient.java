package com.media_sanctum.backend.client.hardcover;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverException;
import com.media_sanctum.backend.client.hardcover.model.Author;
import com.media_sanctum.backend.client.hardcover.model.Book;
import com.media_sanctum.backend.client.hardcover.model.SearchResult;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
public class HardcoverClient {

    static final String INSTANCE = "hardcover";

    private static final String ERRORS_FIELD = "errors";

    private static final String SEARCH_AUTHORS = """
            query SearchAuthors($q: String!) {
                search(query: $q, query_type: "author", per_page: 25, page: 1) {
                    results
                }
            }
            """;

    private static final String SEARCH_BOOKS = """
            query SearchBooks($q: String!) {
                search(query: $q, query_type: "book", per_page: 25, page: 1) {
                    results
                }
            }
            """;

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
    public SearchResult<Author> searchAuthors(String query) {
        JsonNode root = executeQuery(SEARCH_AUTHORS, Map.of("q", safe(query)));
        JsonNode searchResults = root.path("data").path("search").path("results");
        var targetType = objectMapper.getTypeFactory()
                .constructParametricType(SearchResult.class, Author.class);
        return objectMapper.convertValue(searchResults, targetType);
    }

    @RateLimiter(name = INSTANCE)
    public SearchResult<Book> searchBooks(String query) {
        JsonNode root = executeQuery(SEARCH_BOOKS, Map.of("q", safe(query)));
        JsonNode searchResults = root.path("data").path("search").path("results");
        var targetType = objectMapper.getTypeFactory()
                .constructParametricType(SearchResult.class, Book.class);
        return objectMapper.convertValue(searchResults, targetType);
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
