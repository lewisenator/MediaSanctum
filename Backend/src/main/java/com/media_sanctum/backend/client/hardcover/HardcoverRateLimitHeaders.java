package com.media_sanctum.backend.client.hardcover;

import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class HardcoverRateLimitHeaders {

    private HardcoverRateLimitHeaders() {
    }

    static Duration parseRetryAfter(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        Optional<String> retryAfter = Optional.ofNullable(headers.getFirst(HttpHeaders.RETRY_AFTER));
        return retryAfter.flatMap(HardcoverRateLimitHeaders::parseRetryAfterSeconds)
                .or(() -> retryAfter.flatMap(HardcoverRateLimitHeaders::parseRetryAfterHttpDate))
                .or(() -> parseEpochSecondsHeader(headers.getFirst("X-RateLimit-Reset")))
                .orElse(null);
    }

    static Map<String, String> capture(HttpHeaders headers) {
        if (headers == null) {
            return Map.of();
        }
        Map<String, String> captured = new HashMap<>();
        headers.forEach((name, values) -> {
            if (!values.isEmpty()
                    && (name.equalsIgnoreCase("RateLimit")
                    || name.regionMatches(true, 0, "X-RateLimit", 0, "X-RateLimit".length())
                    || name.equalsIgnoreCase(HttpHeaders.RETRY_AFTER))) {
                captured.put(name, values.get(0));
            }
        });
        return captured;
    }

    private static Optional<Duration> parseRetryAfterSeconds(String value) {
        try {
            return Optional.of(Duration.ofSeconds(Long.parseLong(value.trim())));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    private static Optional<Duration> parseRetryAfterHttpDate(String value) {
        try {
            ZonedDateTime resetAt = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
            return Optional.of(nonNegative(Duration.between(ZonedDateTime.now(resetAt.getZone()), resetAt)));
        } catch (DateTimeParseException dtpe) {
            return Optional.empty();
        }
    }

    private static Optional<Duration> parseEpochSecondsHeader(String value) {
        if (value == null) {
            return Optional.empty();
        }
        try {
            long epochSeconds = Long.parseLong(value.trim());
            return Optional.of(nonNegative(Duration.between(Instant.now(), Instant.ofEpochSecond(epochSeconds))));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    private static Duration nonNegative(Duration duration) {
        return duration.isNegative() ? Duration.ZERO : duration;
    }
}
