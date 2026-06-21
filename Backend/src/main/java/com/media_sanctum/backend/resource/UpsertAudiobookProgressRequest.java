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
public class UpsertAudiobookProgressRequest {
    @NotNull
    private Integer percent;

    private Integer currentChapter;
    private Integer totalChapters;

    @NotNull
    private Integer seconds;

    @NotNull
    private Integer duration;
}
