package com.media_sanctum.backend.exception;

import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<?>> handleException(Exception e) {
        var message = e.getMessage();
        var error = ErrorResponse.builder()
                .message(message)
                .error(e.getClass().getSimpleName())
                .timestamp(Instant.now().toString())
                .build();
        var dataResponse = DataResponse.error(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dataResponse);
    }
}
