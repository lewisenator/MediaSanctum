package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnyStringMatcherTest {

    @BeforeEach
    public void setup() {
        MatcherRegistry.getInstance().clear().register(new AnyStringMatcher());
    }

    @Test
    public void testMatch_null() {
        String actual = """
            {
                "test": null
            }
        """;

        String expeted = """
            {
                "test": "{{STRING}}"
            }
        """;

        try {
            JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
        } catch (AssertionError e) {
            Assertions.assertThat(e.getMessage()).contains("JSON Contract Mismatch");
        }
    }

    @Test
    public void testMatch_positive() {
        String actual = """
            {
                "test": "This is a string"
            }
        """;

        String expeted = """
            {
                "test": "{{STRING}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_nested() {
        String actual = """
            {
                "data": {
                    "test": "This is also a string"
                }
            }
        """;

        String expeted = """
            {
                "data": {
                    "test": "{{STRING}}"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }
}
