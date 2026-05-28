package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class TimestampMatcherTest {

    @BeforeEach
    public void setup() {
        MatcherRegistry.getInstance().clear().register(new TimestampMatcher());
    }

    @Test
    public void testMatch_now_ok() {
        String actual = """
            {
                "test": "%s"
            }
        """.formatted(Instant.now().toString());

        String expeted = """
            {
                "test": "{{TIMESTAMP}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_null_contractMismatch() {
        String actual = """
            {
                "test": null
            }
        """;

        String expeted = """
            {
                "test": "{{TIMESTAMP}}"
            }
        """;

        try {
            JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).contains("JSON Contract Mismatch");
        }
    }

    @Test
    public void testMatch_withFiveSecondsOfSkew_ok() {
        String actual = """
            {
                "test": "%s"
            }
        """.formatted(Instant.now().toString());

        String expeted = """
            {
                "test": "{{TIMESTAMP:5:SECONDS}}"
            }
        """;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException _) {}

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }
}
