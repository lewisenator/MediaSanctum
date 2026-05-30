package com.media_sanctum.backend.client.hardcover.exception;

public class HardcoverException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HardcoverException(String message) {
        super(message);
    }

    public HardcoverException(String message, Throwable cause) {
        super(message, cause);
    }
}
