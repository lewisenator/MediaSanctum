package com.media_sanctum.backend.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeriesResponse {
    private Integer hardcoverId;
    private String name;
    private String slug;
    private Boolean isCompleted;
    private Integer booksCount;
    private Long libraryBooksCount;
}
