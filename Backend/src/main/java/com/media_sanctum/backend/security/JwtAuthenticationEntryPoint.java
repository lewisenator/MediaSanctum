package com.media_sanctum.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.model.DataResponse;
import com.media_sanctum.backend.model.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var error = ErrorResponse.builder()
                .error(authException.getClass().getSimpleName())
                .message(authException.getMessage())
                .timestamp(Instant.now().toString())
                .build();
        var dataResponse = DataResponse.error(error);
        
        var errorString = objectMapper.writeValueAsString(dataResponse);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(errorString);
    }
}
