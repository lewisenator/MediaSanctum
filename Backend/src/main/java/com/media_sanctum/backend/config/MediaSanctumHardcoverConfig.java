package com.media_sanctum.backend.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "media-sanctum.hardcover")
@Validated
public record MediaSanctumHardcoverConfig(
        @NotNull String apiKey,
        @NotNull String endpoint
) {
}
