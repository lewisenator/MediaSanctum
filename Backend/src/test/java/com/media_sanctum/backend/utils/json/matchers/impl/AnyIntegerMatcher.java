package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;

public class AnyIntegerMatcher implements JsonValueMatcher {

    private static final String PLACEHOLDER = "{{ANY-INTEGER}}";

    @Override
    public boolean matches(String placeholder) {
        return PLACEHOLDER.equals(placeholder);
    }

    @Override
    public boolean validate(Object actual, String placeholder) {
        return actual instanceof Integer;
    }
}
