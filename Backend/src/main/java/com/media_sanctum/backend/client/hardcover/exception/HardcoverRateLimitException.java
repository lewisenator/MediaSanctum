package com.media_sanctum.backend.client.hardcover.exception;

import java.time.Duration;

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

    public Duration getRetryAfter() {
        return retryAfter;
    }
}
