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
public class AuthorResponse {
    private String hardcoverId;
    private List<String> books;
    private Integer booksCount;
    private String imageUrl;
    private String name;
    private String namePersonal;
    private List<String> seriesNames;
    private String slug;
}
