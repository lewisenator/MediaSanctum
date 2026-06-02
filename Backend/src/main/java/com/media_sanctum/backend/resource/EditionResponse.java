package com.media_sanctum.backend.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditionResponse {
    private String id;
    private String asin;
    private String isbn10;
    private String isbn13;
    private String language;
    private String country;
    private String editionType;
    private Integer pages;
    private Integer audioSeconds;
    private ImageResponse image;
}
