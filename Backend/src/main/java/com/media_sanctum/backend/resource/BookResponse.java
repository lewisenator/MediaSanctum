package com.media_sanctum.backend.resource;

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
    private String hardcoverId;
    private String title;

    private List<String> authors;
    private String description;
    private String featureSeriesName;
    private Float featureSeriesPosition;
    private String imageUrl;
    private Integer releaseYear;
}
