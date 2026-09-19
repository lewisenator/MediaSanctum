package com.media_sanctum.backend.exception;

import com.media_sanctum.backend.client.hardcover.exception.HardcoverException;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverRateLimitException;
import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MediaSanctumConfig config;

    // Client closed the connection mid-stream (seek, pause, tab close). Not an error.
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleClientDisconnect(AsyncRequestNotUsableException e) {
        log.debug("Client disconnected during streaming: {}", e.getMessage());
    }

    @ExceptionHandler(HardcoverRateLimitException.class)
    public ResponseEntity<DataResponse<?>> handleHardcoverRateLimitException(HardcoverRateLimitException e) {
        logHardcoverExhaustion(e);
        return hardcoverUnavailableResponse("HARDCOVER_RATE_LIMITED", e.getMessage());
    }

    @ExceptionHandler(HardcoverException.class)
    public ResponseEntity<DataResponse<?>> handleHardcoverException(HardcoverException e) {
        logHardcoverExhaustion(e);
        return hardcoverUnavailableResponse("HARDCOVER_UNAVAILABLE", e.getMessage());
    }

    private void logHardcoverExhaustion(HardcoverException e) {
        log.warn(
                "Hardcover call exhausted retries: endpoint={} query={} attempts={} elapsed={} "
                        + "rateLimitHeaders={}",
                e.getEndpoint(), e.getQuery(), e.getAttempts(), e.getElapsed(), e.getRateLimitHeaders(), e);
    }

    private ResponseEntity<DataResponse<?>> hardcoverUnavailableResponse(String errorCode, String message) {
        var error = ErrorResponse.builder()
                .message(message)
                .error(errorCode)
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(DataResponse.error(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<?>> handleException(Exception e) {
        if (config.logStackTraces()) {
            log.error(e.getMessage(), e);
        }
        var message = e.getMessage();
        var error = ErrorResponse.builder()
                .message(message)
                .error(e.getClass().getSimpleName())
                .timestamp(LocalDateTime.now().toString())
                .build();
        var dataResponse = DataResponse.error(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dataResponse);
    }
}
