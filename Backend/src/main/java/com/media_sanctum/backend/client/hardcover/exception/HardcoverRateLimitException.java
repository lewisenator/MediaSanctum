package com.media_sanctum.backend.client.hardcover.exception;

import java.time.Duration;

/**
 * Thrown when Hardcover responds with HTTP 429. Carries the wait-duration hint parsed from the
 * {@code Retry-After} header (falling back to {@code X-RateLimit-Reset}), if either was present,
 * so the {@code hardcover-ratelimit} retry can honor it instead of using blind exponential backoff.
 */
public class HardcoverRateLimitException extends HardcoverException {

    private static final long serialVersionUID = 1L;

    private final Duration retryAfter;

    public HardcoverRateLimitException(String message, Duration retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public HardcoverRateLimitException(String message, Duration retryAfter, Throwable cause) {
        super(message, cause);
        this.retryAfter = retryAfter;
    }

    /**
     * The wait-duration hint parsed from the response, or {@code null} if Hardcover didn't send
     * one and the caller should fall back to its own backoff schedule.
     */
    public Duration getRetryAfter() {
        return retryAfter;
    }
}
