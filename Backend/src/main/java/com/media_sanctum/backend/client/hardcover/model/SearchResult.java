package com.media_sanctum.backend.client.hardcover.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult<T> {
    private Integer found;
    private Integer outOf;
    private Integer page;
    private Boolean searchCutoff;
    private Long searchTimeMs;
    private List<SearchHit<T>> hits;
    private RequestParams requestParams;
}
