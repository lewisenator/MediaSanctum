package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

public class AnyStringMatcher implements JsonValueMatcher<String> {

    private static final String PLACEHOLDER = "STRING";

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<String> supportedType() {
        return String.class;
    }

    @Override
    public boolean validate(String path, String actual, MatcherParameters parameters) {
        return true;
    }
}
