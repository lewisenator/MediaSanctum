package com.media_sanctum.backend.client.hardcover.exception;

/**
 * Thrown for non-retryable 4xx responses from Hardcover — e.g. HTTP 403, which Hardcover returns
 * when a query exceeds its 5-query batch limit. These are client-side bugs, not transient
 * failures, so neither the {@code hardcover-ratelimit} nor {@code hardcover-general} retry should
 * retry them; the caller should fail fast instead.
 */
public class HardcoverClientException extends HardcoverException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public HardcoverClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HardcoverClientException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
