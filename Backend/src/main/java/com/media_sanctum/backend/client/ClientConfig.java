package com.media_sanctum.backend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.config.MediaSanctumHardcoverConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    public static final String HARDCOVER_RATELIMIT_INSTANCE = "hardcover-ratelimit";
    public static final String HARDCOVER_GENERAL_INSTANCE = "hardcover-general";

    @Bean
    public HardcoverClient hardCoverClient(
            MediaSanctumHardcoverConfig hardCoverConfig,
            ObjectMapper objectMapper,
            RateLimiterRegistry rateLimiterRegistry,
            RetryRegistry retryRegistry
    ) {
        var restClient = RestClient.builder()
                .baseUrl(hardCoverConfig.endpoint())
                .build();

        RateLimiter hardcoverRateLimiter = rateLimiterRegistry.rateLimiter(HARDCOVER_RATELIMIT_INSTANCE);
        Retry hardcoverRateLimitRetry = retryRegistry.retry(HARDCOVER_RATELIMIT_INSTANCE);
        Retry hardcoverGeneralRetry = retryRegistry.retry(HARDCOVER_GENERAL_INSTANCE);

        return new HardcoverClient(
                hardCoverConfig.apiKey(),
                restClient,
                objectMapper,
                hardCoverConfig.endpoint(),
                hardcoverRateLimiter,
                hardcoverRateLimitRetry,
                hardcoverGeneralRetry
        );
    }

}
