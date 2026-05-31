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
    private String id;
    private String name;
    private String title;
    private List<String> alternateNames;
    private String slug;
    private String bio;
    private Integer bornYear;
    private Integer deathYear;
    private Integer booksCount;
    private List<LinkResponse> links;
    private String createdAt;
    private String updatedAt;
    private ImageResponse image;
}
