package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.client.hardcover.model.HardcoverAuthorSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverBookSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverImage;
import com.media_sanctum.backend.client.hardcover.model.HardcoverRequestParams;
import com.media_sanctum.backend.client.hardcover.model.HardcoverSearchHit;
import com.media_sanctum.backend.client.hardcover.model.HardcoverSearchResult;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class SearchControllerTest extends BaseControllerTest {

    @MockitoBean
    private HardcoverClient hardcoverClient;

    private Function<String, String> encode = (String query) -> URLEncoder.encode(query, StandardCharsets.UTF_8);

    @Test
    public void searchAuthors_ok() {
        var author = HardcoverAuthorSearchResult.builder()
                .id("233073")
                .name("Craig Alanson")
                .image(HardcoverImage.builder().url("https://example.test/craig.jpg").build())
                .build();
        when(hardcoverClient.searchAuthors("Craig Alanson")).thenReturn(
                HardcoverSearchResult.<HardcoverAuthorSearchResult>builder()
                        .found(1)
                        .page(1)
                        .requestParams(HardcoverRequestParams.builder().perPage(20).build())
                        .hits(List.of(HardcoverSearchHit.<HardcoverAuthorSearchResult>builder().document(author).build()))
                        .build());

        var response = restClient.post()
                .uri("/api/search/authors?q=" + encode.apply("Craig Alanson"))
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(new ParameterizedTypeReference<DataResponse<SearchResponse<AuthorSearchResultResponse>>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        var hits = response.getBody().getData().getHits();
        assertThat(hits).isNotNull();

        Assertions.assertThat(hits).extracting("hardcoverId").containsExactly("233073");
        Assertions.assertThat(hits).extracting("name").containsExactly("Craig Alanson");
    }

    @Test
    public void searchBooks_ok() {
        var book = HardcoverBookSearchResult.builder()
                .id("427970")
                .title("Columbus Day")
                .build();
        when(hardcoverClient.searchBooks("Columbus Day")).thenReturn(
                HardcoverSearchResult.<HardcoverBookSearchResult>builder()
                        .found(1)
                        .page(1)
                        .requestParams(HardcoverRequestParams.builder().perPage(20).build())
                        .hits(List.of(HardcoverSearchHit.<HardcoverBookSearchResult>builder().document(book).build()))
                        .build());

        var response = restClient.post()
                .uri("/api/search/books?q=" + encode.apply("Columbus Day"))
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(new ParameterizedTypeReference<DataResponse<SearchResponse<BookSearchResultResponse>>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        var hits = response.getBody().getData().getHits();
        assertThat(hits).isNotNull();

        Assertions.assertThat(hits).extracting("hardcoverId").containsExactly("427970");
        Assertions.assertThat(hits).extracting("title").containsExactly("Columbus Day");
    }
}
