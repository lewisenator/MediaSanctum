package com.media_sanctum.backend.client.hardcover.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HardcoverBook {
    private Integer id;
    private Integer canonicalId;
    private String headline;
    private String title;
    private String slug;
    private String subtitle;
    private String description;
    private Integer releaseYear;
    private Integer pages;
    private Integer audioSeconds;
    private Float rating;
    private Integer ratingsCount;
    private List<HardcoverTagging> taggings;
    private HardcoverEdition defaultCoverEdition;
    private HardcoverEdition defaultAudioEdition;
    private List<HardcoverContribution> contributions;
    private HardcoverFeaturedSeriesSearchResult featuredBookSeries;

    public boolean isNonCanonical() {
        return canonicalId != null && id != null && !canonicalId.equals(id);
    }

    public Optional<Integer> getAuthorHardcoverId() {
        return Optional.ofNullable(contributions)
                .map(List::getFirst)
                .map(HardcoverContribution::getAuthor)
                .map(HardcoverAuthorContribution::getId);
    }

    public List<HardcoverTag> getSimpleTags() {
        return Optional.ofNullable(taggings)
                .orElse(List.of())
                .stream()
                .map(HardcoverTagging::getTag)
                .toList();
    }
}
