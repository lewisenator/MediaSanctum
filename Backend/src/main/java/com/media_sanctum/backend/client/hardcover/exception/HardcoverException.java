package com.media_sanctum.backend.client.hardcover.exception;

import java.time.Duration;
import java.util.Map;

public class HardcoverException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String endpoint;
    private String query;
    private int attempts;
    private Duration elapsed;
    private Map<String, String> rateLimitHeaders = Map.of();

    public HardcoverException(String message) {
        super(message);
    }

    public HardcoverException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Attaches retry-exhaustion diagnostics to this exception in place, so the same instance
     * (preserving its original type, message, cause, and stack trace) can be rethrown for a
     * handler to log without needing visibility into the retry machinery that produced it.
     */
    public HardcoverException withDiagnostics(
            String endpoint, String query, int attempts, Duration elapsed, Map<String, String> rateLimitHeaders
    ) {
        this.endpoint = endpoint;
        this.query = query;
        this.attempts = attempts;
        this.elapsed = elapsed;
        this.rateLimitHeaders = rateLimitHeaders != null ? rateLimitHeaders : Map.of();
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getQuery() {
        return query;
    }

    public int getAttempts() {
        return attempts;
    }

    public Duration getElapsed() {
        return elapsed;
    }

    public Map<String, String> getRateLimitHeaders() {
        return rateLimitHeaders;
    }
}
