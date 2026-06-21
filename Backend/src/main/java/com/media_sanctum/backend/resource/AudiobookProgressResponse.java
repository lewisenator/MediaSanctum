package com.media_sanctum.backend.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudiobookProgressResponse {
    private String id;
    private Integer percent;
    private Integer currentChapter;
    private Integer totalChapters;
    private Integer seconds;
    private Integer duration;
    private String createdAt;
    private String updatedAt;
}
