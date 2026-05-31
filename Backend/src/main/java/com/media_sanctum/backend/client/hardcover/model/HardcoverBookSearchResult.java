package com.media_sanctum.backend.client.hardcover.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HardcoverBookSearchResult {
    private List<String> authorNames;
    private List<HardcoverContribution> contributions;
    private String description;
    private HardcoverFeaturedSeriesSearchResult featuredSeries;
    private List<String> genres;
    private Boolean hasAudiobook;
    private Boolean hasEbook;
    private String id;
    private HardcoverImage image;
    private List<String> isbns;
    private List<String> moods;
    private Integer pages;
    private Double rating;
    private Integer ratingsCount;
    private String releaseDate;
    private Integer releaseYear;
    private List<Integer> seriesIds;
    private List<String> seriesNames;
    private String slug;
    private List<String> tags;
    private String title;

    public Optional<HardcoverAuthorContribution> getAuthorContribution() {
        return Optional.ofNullable(contributions).orElse(List.of()).stream()
                .map(HardcoverContribution::getAuthor)
                .filter(Objects::nonNull)
                .findFirst();
    }
}
