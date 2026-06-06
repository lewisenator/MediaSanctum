package com.media_sanctum.backend.resource;

import com.media_sanctum.backend.entity.EditionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressResponse {
    private String id;
    private EditionType editionType;
    private String epubcfi;
    private Integer percent;
    private Integer currentChapter;
    private Integer totalChapters;
    private Integer currentPage;
    private Integer totalPages;
    private String createdAt;
    private String updatedAt;
}
