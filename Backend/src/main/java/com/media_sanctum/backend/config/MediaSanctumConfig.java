package com.media_sanctum.backend.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "media-sanctum")
@Validated
public record MediaSanctumConfig(
        @NotBlank String dataDir,
        @NotBlank String configDir,
        @NotNull Boolean cookiesSecure,
        @NotNull MediaSanctumJwtConfig jwt
) {
}
