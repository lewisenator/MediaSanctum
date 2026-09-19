package com.media_sanctum.backend.client.hardcover;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverClientException;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverException;
import com.media_sanctum.backend.client.hardcover.exception.HardcoverRateLimitException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HardcoverClientTest {

    private static final String ENDPOINT = "https://api.hardcover.test/v1/graphql";
    private static final String AUTHOR_SUCCESS_BODY =
            "{\"data\":{\"authors_by_pk\":{\"id\":1,\"name\":\"Test Author\"}}}";

    private MockRestServiceServer server;
    private HardcoverClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(ENDPOINT);
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        RateLimiter rateLimiter = RateLimiter.of("hardcover-ratelimit-test", RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(60))
                .timeoutDuration(Duration.ofMillis(50))
                .build());

        Retry generalRetry = Retry.of("hardcover-general-test", RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.of(1))
                .ignoreExceptions(HardcoverRateLimitException.class, HardcoverClientException.class)
                .build());

        Retry rateLimitRetry = Retry.of("hardcover-ratelimit-retry-test", RetryConfig.custom()
                .maxAttempts(3)
                .retryExceptions(HardcoverRateLimitException.class)
                .intervalBiFunction((attempt, either) -> {
                    if (either.isLeft() && either.getLeft() instanceof HardcoverRateLimitException rle
                            && rle.getRetryAfter() != null) {
                        return rle.getRetryAfter().toMillis();
                    }
                    return 1L;
                })
                .build());

        client = new HardcoverClient(
                "test-api-key",
                restClient,
                new ObjectMapper(),
                ENDPOINT,
                rateLimiter,
                rateLimitRetry,
                generalRetry
        );
    }

    @Test
    void getAuthor_ok() {
        server.expect(anything())
                .andRespond(withSuccess(AUTHOR_SUCCESS_BODY, MediaType.APPLICATION_JSON));

        var author = client.getAuthor(1);

        assertThat(author.getName()).isEqualTo("Test Author");
    }

    @Test
    void getAuthor_429_parsesRetryAfterSeconds_andSucceedsAfterRetry() {
        server.expect(anything())
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .header(HttpHeaders.RETRY_AFTER, "0")
                        .body("rate limited"));
        server.expect(anything())
                .andRespond(withSuccess(AUTHOR_SUCCESS_BODY, MediaType.APPLICATION_JSON));

        var author = client.getAuthor(1);

        assertThat(author.getName()).isEqualTo("Test Author");
        server.verify();
    }

    @Test
    void getAuthor_429_noRetryAfter_fallsBackToXRateLimitReset() {
        long resetEpochSeconds = java.time.Instant.now().getEpochSecond();
        server.expect(anything())
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .header("X-RateLimit-Reset", String.valueOf(resetEpochSeconds))
                        .body("rate limited"));
        server.expect(anything())
                .andRespond(withSuccess(AUTHOR_SUCCESS_BODY, MediaType.APPLICATION_JSON));

        var author = client.getAuthor(1);

        assertThat(author.getName()).isEqualTo("Test Author");
        server.verify();
    }

    @Test
    void getAuthor_429_exhaustsRateLimitRetry_throwsEnrichedRateLimitException() {
        for (int i = 0; i < 3; i++) {
            server.expect(anything())
                    .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                            .header(HttpHeaders.RETRY_AFTER, "0")
                            .header("X-RateLimit-Limit", "60")
                            .body("rate limited"));
        }

        assertThatThrownBy(() -> client.getAuthor(1))
                .isInstanceOf(HardcoverRateLimitException.class)
                .satisfies(ex -> {
                    var rle = (HardcoverRateLimitException) ex;
                    assertThat(rle.getAttempts()).isEqualTo(3);
                    assertThat(rle.getEndpoint()).isEqualTo(ENDPOINT);
                    assertThat(rle.getQuery()).isNotBlank();
                    assertThat(rle.getElapsed()).isNotNull();
                    assertThat(rle.getRateLimitHeaders()).containsEntry("X-RateLimit-Limit", "60");
                });
        server.verify();
    }

    @Test
    void getAuthor_403_throwsNonRetryableClientException_doesNotRetry() {
        server.expect(anything())
                .andRespond(withStatus(HttpStatus.FORBIDDEN).body("batch limit exceeded"));

        assertThatThrownBy(() -> client.getAuthor(1))
                .isInstanceOf(HardcoverClientException.class)
                .satisfies(ex -> assertThat(((HardcoverClientException) ex).getStatusCode()).isEqualTo(403));

        server.verify();
    }

    @Test
    void getAuthor_5xx_retriesThenSucceeds() {
        server.expect(anything())
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE).body("down"));
        server.expect(anything())
                .andRespond(withSuccess(AUTHOR_SUCCESS_BODY, MediaType.APPLICATION_JSON));

        var author = client.getAuthor(1);

        assertThat(author.getName()).isEqualTo("Test Author");
        server.verify();
    }

    @Test
    void getAuthor_5xx_exhaustsGeneralRetry_throwsEnrichedHardcoverException() {
        for (int i = 0; i < 3; i++) {
            server.expect(anything())
                    .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE).body("down"));
        }

        assertThatThrownBy(() -> client.getAuthor(1))
                .isInstanceOf(HardcoverException.class)
                .isNotInstanceOf(HardcoverRateLimitException.class)
                .isNotInstanceOf(HardcoverClientException.class)
                .satisfies(ex -> {
                    var he = (HardcoverException) ex;
                    assertThat(he.getAttempts()).isEqualTo(3);
                    assertThat(he.getEndpoint()).isEqualTo(ENDPOINT);
                    assertThat(he.getElapsed()).isNotNull();
                });

        server.verify();
    }
}
