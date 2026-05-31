package com.media_sanctum.backend.service;

import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.client.hardcover.model.HardcoverFeaturedSeriesSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverImage;
import com.media_sanctum.backend.client.hardcover.model.HardcoverSeries;
import com.media_sanctum.backend.resource.AuthorSearchResultResponse;
import com.media_sanctum.backend.resource.BookSearchResultResponse;
import com.media_sanctum.backend.resource.SearchResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    private final HardcoverClient hardcoverClient;

    public SearchService(HardcoverClient hardcoverClient) {
        this.hardcoverClient = hardcoverClient;
    }

    public SearchResponse<AuthorSearchResultResponse> searchAuthors(String query) {
        var result = hardcoverClient.searchAuthors(query);

        List<AuthorSearchResultResponse> hits = result.getHits().stream().map(hit -> {
            var doc = hit.getDocument();
            return AuthorSearchResultResponse.builder()
                    .hardcoverId(doc.getId())
                    .books(doc.getBooks())
                    .booksCount(doc.getBooksCount())
                    .imageUrl(doc.getImage().getUrl())
                    .name(doc.getName())
                    .namePersonal(doc.getNamePersonal())
                    .seriesNames(doc.getSeriesNames())
                    .slug(doc.getSlug())
                    .build();
        }).toList();

        return SearchResponse.<AuthorSearchResultResponse>builder()
                .found(result.getFound())
                .page(result.getPage())
                .perPage(result.getRequestParams().getPerPage())
                .hits(hits)
                .build();
    }

    public SearchResponse<BookSearchResultResponse> searchBooks(String query) {
        var result = hardcoverClient.searchBooks(query);

        List<BookSearchResultResponse> hits = result.getHits().stream().map(hit -> {
            var doc = hit.getDocument();
            return BookSearchResultResponse.builder()
                    .hardcoverId(doc.getId())
                    .title(doc.getTitle())
                    .description(doc.getDescription())
                    .authors(doc.getAuthorNames())
                    .featureSeriesName(Optional.ofNullable(doc.getFeaturedSeries())
                            .map(HardcoverFeaturedSeriesSearchResult::getSeries)
                            .map(HardcoverSeries::getName)
                            .orElse(null))
                    .featureSeriesPosition(Optional.ofNullable(doc.getFeaturedSeries())
                            .map(HardcoverFeaturedSeriesSearchResult::getPosition)
                            .orElse(null))
                    .imageUrl(Optional.ofNullable(doc.getImage())
                            .map(HardcoverImage::getUrl)
                            .orElse(null))
                    .releaseYear(doc.getReleaseYear())
                    .build();
        }).toList();
        return SearchResponse.<BookSearchResultResponse>builder()
                .found(result.getFound())
                .page(result.getPage())
                .perPage(result.getRequestParams().getPerPage())
                .hits(hits)
                .build();
    }
}
