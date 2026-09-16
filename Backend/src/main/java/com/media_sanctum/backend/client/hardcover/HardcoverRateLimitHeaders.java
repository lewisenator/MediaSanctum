package com.media_sanctum.backend.client.hardcover;

import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses Hardcover's rate-limit response headers ({@code Retry-After}, {@code X-RateLimit-Reset},
 * and the other {@code RateLimit}/{@code X-RateLimit-*} headers) so {@link HardcoverClient} can
 * honor a server-provided wait hint instead of blindly retrying on its own backoff schedule.
 */
final class HardcoverRateLimitHeaders {

    private HardcoverRateLimitHeaders() {
    }

    static Duration parseRetryAfter(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        String retryAfter = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (retryAfter != null) {
            Duration fromSeconds = parseRetryAfterSeconds(retryAfter);
            if (fromSeconds != null) {
                return fromSeconds;
            }
            Duration fromDate = parseRetryAfterHttpDate(retryAfter);
            if (fromDate != null) {
                return fromDate;
            }
        }
        return parseEpochSecondsHeader(headers.getFirst("X-RateLimit-Reset"));
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

    private static Duration parseRetryAfterSeconds(String value) {
        try {
            return Duration.ofSeconds(Long.parseLong(value.trim()));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private static Duration parseRetryAfterHttpDate(String value) {
        try {
            ZonedDateTime resetAt = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
            Duration duration = Duration.between(ZonedDateTime.now(resetAt.getZone()), resetAt);
            return duration.isNegative() ? Duration.ZERO : duration;
        } catch (DateTimeParseException dtpe) {
            return null;
        }
    }

    private static Duration parseEpochSecondsHeader(String value) {
        if (value == null) {
            return null;
        }
        try {
            long epochSeconds = Long.parseLong(value.trim());
            Duration duration = Duration.between(Instant.now(), Instant.ofEpochSecond(epochSeconds));
            return duration.isNegative() ? Duration.ZERO : duration;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
