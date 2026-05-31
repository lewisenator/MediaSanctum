package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.resource.AuthorSearchResultResponse;
import com.media_sanctum.backend.resource.BookSearchResultResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.SearchResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchControllerTest extends BaseControllerTest {

    private Function<String, String> encode = (String query) -> URLEncoder.encode(query, StandardCharsets.UTF_8);

    @Test
    public void searchAuthors_ok() {
        var response = restClient.post()
                .uri("/api/search/authors?q=" + encode.apply("Craig Alanson"))
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(new ParameterizedTypeReference<DataResponse<SearchResponse<AuthorSearchResultResponse>>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        var hits = response.getBody().getData().getHits();
        assertThat(hits).isNotNull();

        Assertions.assertThat(hits).extracting("hardcoverId").contains("233073");
    }

    @Test
    public void searchBooks_ok() {
        var response = restClient.post()
                .uri("/api/search/books?q=" + encode.apply("Columbus Day"))
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(new ParameterizedTypeReference<DataResponse<SearchResponse<BookSearchResultResponse>>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        var hits = response.getBody().getData().getHits();
        assertThat(hits).isNotNull();

        Assertions.assertThat(hits).extracting("hardcoverId").contains("427970");
    }
}
