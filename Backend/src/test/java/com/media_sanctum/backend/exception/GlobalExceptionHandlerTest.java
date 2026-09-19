package com.media_sanctum.backend.exception;

import com.media_sanctum.backend.client.hardcover.exception.HardcoverException;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverRateLimitException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleHardcoverRateLimitException_returns503WithRateLimitedErrorCode() {
        var ex = (HardcoverRateLimitException) new HardcoverRateLimitException("429 from hardcover", Duration.ofSeconds(5))
                .withDiagnostics(
                        "https://api.hardcover.app/v1/graphql",
                        "query { search }",
                        3,
                        Duration.ofMillis(1200),
                        Map.of("X-RateLimit-Limit", "60"));

        var response = handler.handleHardcoverRateLimitException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getError().getError()).isEqualTo("HARDCOVER_RATE_LIMITED");
    }

    @Test
    void handleHardcoverException_returns503WithUnavailableErrorCode() {
        var ex = (HardcoverException) new HardcoverException("transient failure", new RuntimeException("boom"))
                .withDiagnostics(
                        "https://api.hardcover.app/v1/graphql",
                        "query { search }",
                        3,
                        Duration.ofMillis(900),
                        Map.of());

        var response = handler.handleHardcoverException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getError().getError()).isEqualTo("HARDCOVER_UNAVAILABLE");
    }
}
