package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringArrayMatcherTest {


    @BeforeEach
    public void setup() {
        MatcherRegistry.getInstance().clear().register(new StringArrayMatcher());
    }

    @Test
    public void testMatch_matches() {
        String actual = """
            {
                "test": ["string1", "string2"]
            }
        """;

        String expeted = """
            {
                "test": "{{STRING-ARRAY}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_empty_matches() {
        String actual = """
            {
                "test": []
            }
        """;

        String expeted = """
            {
                "test": "{{STRING-ARRAY}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_empty_disallowEmpty_doesntMatch() {
        String actual = """
            {
                "test": []
            }
        """;

        String expeted = """
            {
                "test": "{{STRING-ARRAY?allowEmpty=false}}"
            }
        """;

        try {
            JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
        } catch (AssertionError e) {
            return;
        }
        Assertions.fail("Expect empty to fail STRING-ARRAY matcher when allowEmpty=false");
    }
}
