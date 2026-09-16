package com.media_sanctum.backend.config;

import com.media_sanctum.backend.client.hardcover.exception.HardcoverRateLimitException;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * The {@code hardcover-ratelimit} retry instance needs a wait time resilience4j's YAML-driven
 * config can't express: when Hardcover tells us how long to wait (via {@code Retry-After} /
 * {@code X-RateLimit-Reset}), honor that instead of the profile's exponential backoff. This
 * customizer runs after the YAML-bound {@code RetryConfig} is built and swaps in a dynamic
 * interval function that prefers the hint carried on {@link HardcoverRateLimitException} and
 * falls back to the same exponential shape the profile's YAML would otherwise have configured.
 */
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

    /**
     * {@link RetryConfigCustomizer#customize} hands us a raw {@code RetryConfig.Builder}; this
     * generic helper recovers a properly typed builder so {@code intervalBiFunction} can be
     * called without erasing {@code Either}/{@code Integer} to {@code Object}.
     */
    private static <T> void applyRetryAfterAwareInterval(RetryConfig.Builder<T> builder, IntervalFunction fallback) {
        builder.intervalBiFunction((attempt, either) -> {
            boolean isRateLimited = either.isLeft()
                    && either.getLeft() instanceof HardcoverRateLimitException rle
                    && rle.getRetryAfter() != null;
            if (isRateLimited) {
                return ((HardcoverRateLimitException) either.getLeft()).getRetryAfter().toMillis();
            }
            return fallback.apply(attempt);
        });
    }
}
