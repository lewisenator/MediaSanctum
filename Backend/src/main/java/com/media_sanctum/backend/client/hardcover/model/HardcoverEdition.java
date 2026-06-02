package com.media_sanctum.backend.client.hardcover.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HardcoverEdition {
    private Integer id;
    private Integer canonicalId;
    private Integer bookId;
    private String asin;
    private HardcoverImage cachedImage;
    private String isbn10;
    private String isbn13;
    private HardcoverLanguage language;
    private HardcoverCountry country;
    private Integer pages;
    private Integer audioSeconds;
}
