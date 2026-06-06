package com.media_sanctum.backend.resource;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpsertProgressRequest {

    @NotNull
    private String epubcfi;

    @NotNull
    private Integer percent;

    private Integer currentChapter;
    private Integer totalChapters;

    private Integer currentPage;
    private Integer totalPages;
}
