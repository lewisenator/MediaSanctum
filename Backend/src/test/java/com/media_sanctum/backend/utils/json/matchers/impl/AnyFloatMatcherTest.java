package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnyFloatMatcherTest {

    @BeforeEach
    public void setup() {
        MatcherRegistry.getInstance().clear().register(new AnyFloatMatcher());
    }

    @Test
    public void testMatch_ok() {
        String actual = """
            {
                "test": 4.1212044
            }
        """;

        String expeted = """
            {
                "test": "{{FLOAT}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }
}
