package com.media_sanctum.backend.service;

import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.client.hardcover.model.FeaturedSeries;
import com.media_sanctum.backend.client.hardcover.model.Image;
import com.media_sanctum.backend.client.hardcover.model.Series;
import com.media_sanctum.backend.resource.AuthorResponse;
import com.media_sanctum.backend.resource.BookResponse;
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

    public SearchResponse<AuthorResponse> searchAuthors(String query) {
        var result = hardcoverClient.searchAuthors(query);

        List<AuthorResponse> hits = result.getHits().stream().map(hit -> {
            var doc = hit.getDocument();
            return AuthorResponse.builder()
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

        return SearchResponse.<AuthorResponse>builder()
                .found(result.getFound())
                .page(result.getPage())
                .perPage(result.getRequestParams().getPerPage())
                .hits(hits)
                .build();
    }

    public SearchResponse<BookResponse> searchBooks(String query) {
        var result = hardcoverClient.searchBooks(query);

        List<BookResponse> hits = result.getHits().stream().map(hit -> {
            var doc = hit.getDocument();
            return BookResponse.builder()
                    .hardcoverId(doc.getId())
                    .title(doc.getTitle())
                    .description(doc.getDescription())
                    .authors(doc.getAuthorNames())
                    .featureSeriesName(Optional.ofNullable(doc.getFeaturedSeries())
                            .map(FeaturedSeries::getSeries)
                            .map(Series::getName)
                            .orElse(null))
                    .featureSeriesPosition(Optional.ofNullable(doc.getFeaturedSeries())
                            .map(FeaturedSeries::getPosition)
                            .orElse(null))
                    .imageUrl(Optional.ofNullable(doc.getImage())
                            .map(Image::getUrl)
                            .orElse(null))
                    .releaseYear(doc.getReleaseYear())
                    .build();
        }).toList();
        return SearchResponse.<BookResponse>builder()
                .found(result.getFound())
                .page(result.getPage())
                .perPage(result.getRequestParams().getPerPage())
                .hits(hits)
                .build();
    }
}
