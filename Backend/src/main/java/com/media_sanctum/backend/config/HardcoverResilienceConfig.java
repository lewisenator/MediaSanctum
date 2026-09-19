package com.media_sanctum.backend.config;

import com.media_sanctum.backend.client.hardcover.exception.HardcoverRateLimitException;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class HardcoverResilienceConfig {

    public static final String RATE_LIMIT_INSTANCE = "hardcover-ratelimit";

    @Bean
    public RetryConfigCustomizer hardcoverRateLimitRetryConfigCustomizer(
            @Value("${resilience4j.retry.instances.hardcover-ratelimit.wait-duration}")
            Duration fallbackWaitDuration,
            @Value("${resilience4j.retry.instances.hardcover-ratelimit.exponential-backoff-multiplier}")
            double fallbackBackoffMultiplier
    ) {
        IntervalFunction fallback =
                IntervalFunction.ofExponentialBackoff(fallbackWaitDuration, fallbackBackoffMultiplier);
        return RetryConfigCustomizer.of(
                RATE_LIMIT_INSTANCE, builder -> applyRetryAfterAwareInterval(builder, fallback));
    }

    private static <T> void applyRetryAfterAwareInterval(RetryConfig.Builder<T> builder, IntervalFunction fallback) {
        builder.intervalBiFunction((attempt, either) -> either.isLeft()
                && either.getLeft() instanceof HardcoverRateLimitException rle
                && rle.getRetryAfter() != null
                ? rle.getRetryAfter().toMillis()
                : fallback.apply(attempt));
    }
}
