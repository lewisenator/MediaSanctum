package com.media_sanctum.backend.entity.audio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Chapter {
    private Integer id;
    private String timeBase;
    private Integer start;
    private String startTime;
    private Integer end;
    private String endTime;
    private Map<String, String> tags;

    public String getTitle() {
        return Optional.ofNullable(tags)
                .map(x -> x.get("title"))
                .orElse(null);
    }
}
