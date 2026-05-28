package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;

public class IgnoreMatcher implements JsonValueMatcher {

    private static final String PLACEHOLDER = "{{IGNORE}}";

    @Override
    public boolean matches(String placeholder) {
        return PLACEHOLDER.equals(placeholder);
    }

    @Override
    public boolean validate(Object actual, String placeholder) {
        // This matcher ignores the actual value and always returns true
        return true;
    }
}
