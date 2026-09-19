package com.media_sanctum.backend.client.hardcover.exception;

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
