package com.media_sanctum.backend.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "media-sanctum.log")
@Validated
public record MediaSanctumLogConfig(
        @NotBlank String path
) {}
