package com.media_sanctum.backend.config;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public record MediaSanctumJwtExpirationConfig(
        @NotNull ChronoUnit timeUnit,
        @NotNull Integer value
) {

    public Duration getDuration() {
        return Duration.of(value, timeUnit);
    }
}
