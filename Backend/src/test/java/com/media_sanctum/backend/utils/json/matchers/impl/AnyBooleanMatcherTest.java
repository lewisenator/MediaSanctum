package com.media_sanctum.backend.utils.json.matchers.impl;


import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                "test": "{{BOOLEAN}}"
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
                "test": "{{BOOLEAN}}"
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
                    "test": "{{BOOLEAN}}"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }

    @Test
    public void testMatch_null_fails() {
        String actual = """
            {
                "test": null
            }
        """;

        String expeted = """
            {
                "test": "{{BOOLEAN}}"
            }
        """;

        try {
            JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
        } catch (AssertionError e) {
            return;
        }
        Assertions.fail("Expect null to fail BOOLEAN matcher");
    }

    @Test
    public void testMatch_null_nullable_succeeds() {
        String actual = """
            {
                "test": null
            }
        """;

        String expeted = """
            {
                "test": "{{BOOLEAN?nullable=true}}"
            }
        """;

        JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
    }


    @Test
    public void testMatch_string_fails() {
        String actual = """
            {
                "test": "not a boolean"
            }
        """;

        String expeted = """
            {
                "test": "{{BOOLEAN}}"
            }
        """;

        try {
            JsonAssertionBuilder.assertThatJson(actual).matchesContract(expeted);
        } catch (AssertionError e) {
            return;
        }
        Assertions.fail("Expect string to fail BOOLEAN matcher");
    }
}
