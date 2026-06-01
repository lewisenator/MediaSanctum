package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

public class IgnoreMatcher implements JsonValueMatcher<Object> {

    private static final String PLACEHOLDER = "IGNORE";

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<Object> supportedType() {
        return Object.class;
    }

    @Override
    public boolean validate(String path, Object actual, MatcherParameters parameters) {
        // This matcher ignores the actual value and always returns true
        return true;
    }
}
