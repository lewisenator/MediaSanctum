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
public class SearchResponse<T> {
    private Integer found;
    private Integer page;
    private Integer perPage;
    private List<T> hits;
}
