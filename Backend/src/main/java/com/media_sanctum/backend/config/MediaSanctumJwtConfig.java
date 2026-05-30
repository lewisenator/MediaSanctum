package com.media_sanctum.backend.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "media-sanctum.jwt")
public record MediaSanctumJwtConfig(
        // Must be 256 bits / 32 bytes
        @NotNull String secret,
        @NotNull MediaSanctumJwtExpirationConfig accessExp,
        @NotNull MediaSanctumJwtExpirationConfig refreshExp
) {
}
