package com.media_sanctum.backend.utils.json.matchers.impl;


import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

public class AnyBooleanMatcherTest {

    @BeforeEach
    public void setup() {
        MatcherRegistry.getInstance().clear().register(new AnyBooleanMatcher());
    }

    @Test
    public void testMatch_true() {
        String actual = """
            {
                "test": true
            }
        """;

        String expeted = """
            {
                "test": "{{ANY-BOOLEAN}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_false() {
        String actual = """
            {
                "test": false
            }
        """;

        String expeted = """
            {
                "test": "{{ANY-BOOLEAN}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_nested() {
        String actual = """
            {
                "data": {
                    "test": true
                }
            }
        """;

        String expeted = """
            {
                "data": {
                    "test": "{{ANY-BOOLEAN}}"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }
}
