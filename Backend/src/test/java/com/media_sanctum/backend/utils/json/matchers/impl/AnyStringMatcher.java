package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;

public class AnyStringMatcher implements JsonValueMatcher {

    private static final String PLACEHOLDER = "{{ANY-STRING}}";

    @Override
    public boolean matches(String placeholder) {
        return PLACEHOLDER.equals(placeholder);
    }

    @Override
    public boolean validate(Object actual, String placeholder) {
        return actual instanceof String;
    }
}
