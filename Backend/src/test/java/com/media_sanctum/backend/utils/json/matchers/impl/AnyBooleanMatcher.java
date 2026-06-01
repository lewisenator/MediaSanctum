package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

public class AnyBooleanMatcher implements JsonValueMatcher<Boolean> {

    private static final String PLACEHOLDER = "BOOLEAN";

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<Boolean> supportedType() {
        return Boolean.class;
    }

    @Override
    public boolean validate(String path, Boolean actual, MatcherParameters parameters) {
        return true;
    }
}
