package com.media_sanctum.backend.resource;

import com.media_sanctum.backend.entity.EditionType;
import com.media_sanctum.backend.entity.audio.FFProbe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookFileResponse {
    private String id;
    private String url;
    private Long size;
    private String filename;
    private String contentType;
    private EditionType editionType;
    private FFProbe ffProbe;
    private String createdAt;
    private String updatedAt;
}
