package com.media_sanctum.backend.utils.json.matchers.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;

import java.util.Optional;

public class AnyBooleanMatcher implements JsonValueMatcher {

    private static final String PLACEHOLDER = "{{ANY-BOOLEAN}}";

    @Override
    public boolean matches(String placeholder) {
        return PLACEHOLDER.equals(placeholder);
    }

    @Override
    public boolean validate(Object actual, String placeholder) {
        return actual instanceof Boolean;
    }
}
