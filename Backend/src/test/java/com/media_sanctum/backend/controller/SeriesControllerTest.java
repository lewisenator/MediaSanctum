package com.media_sanctum.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.client.hardcover.model.HardcoverFeaturedSeriesSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverSeries;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.repository.BookRepository;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.SeriesResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeriesControllerTest extends BaseControllerTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getSeries_derivedFromBooks() throws Exception {
        var seriesId = Math.abs(UUID.randomUUID().hashCode()) + 1_000_000;
        var name = "Test Series " + UUID.randomUUID();
        saveBook("Book One", featured(seriesId, name));
        saveBook("Book Two", featured(seriesId, name));
        saveBook("Standalone", null);

        var response = restClient.get()
                .uri("/api/series")
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var type = new TypeReference<DataResponse<List<SeriesResponse>>>() {};
        var series = objectMapper.readValue(response.getBody(), type).getData();
        var matching = series.stream().filter(s -> s.getHardcoverId() == seriesId).toList();

        assertThat(matching).hasSize(1);
        assertThat(matching.getFirst().getName()).isEqualTo(name);
        assertThat(matching.getFirst().getBooksCount()).isEqualTo(5);
        assertThat(matching.getFirst().getLibraryBooksCount()).isEqualTo(2L);
    }

    @Test
    public void getSeries_requiresAuth() {
        var response = restClient.get()
                .uri("/api/series")
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private void saveBook(String title, HardcoverFeaturedSeriesSearchResult featured) {
        bookRepository.save(Book.builder().title(title).featuredSeries(featured).build());
    }

    private static HardcoverFeaturedSeriesSearchResult featured(int id, String name) {
        var series = HardcoverSeries.builder()
                .id(id).name(name).slug("slug-" + id).booksCount(5).isCompleted(false).build();
        return HardcoverFeaturedSeriesSearchResult.builder().id("f" + id).position(1f).series(series).build();
    }
}
