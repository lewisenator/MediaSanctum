package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnyIntegerMatcherTest {

    @BeforeEach
    public void setup() {
        MatcherRegistry.getInstance().clear().register(new AnyIntegerMatcher());
    }

    @Test
    public void testMatch_negative() {
        String actual = """
            {
                "test": -1
            }
        """;

        String expeted = """
            {
                "test": "{{INTEGER}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_positive() {
        String actual = """
            {
                "test": 4
            }
        """;

        String expeted = """
            {
                "test": "{{INTEGER}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_nested() {
        String actual = """
            {
                "data": {
                    "test": 99
                }
            }
        """;

        String expeted = """
            {
                "data": {
                    "test": "{{INTEGER}}"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }
}
