package com.media_sanctum.backend.resource;

import com.media_sanctum.backend.client.hardcover.model.HardcoverFeaturedSeriesSearchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private String id;
    private String headline;
    private String title;
    private String slug;
    private String subtitle;
    private String description;
    private Integer releaseYear;
    private Integer pages;
    private Integer audioSeconds;
    private String createdAt;
    private String updatedAt;
    private Float rating;
    private Integer ratingsCount;
    private List<String> tags;
    private AuthorResponse author;
    private EditionResponse ebookEdition;
    private EditionResponse audiobookEdition;
    private HardcoverFeaturedSeriesSearchResult featuredSeries;
    private BookFileResponse ebookFile;
    private BookFileResponse audiobookFile;
}
