package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

public class AnyIntegerMatcher implements JsonValueMatcher<Integer> {

    private static final String PLACEHOLDER = "INTEGER";

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<Integer> supportedType() {
        return Integer.class;
    }

    @Override
    public boolean validate(String path, Integer actual, MatcherParameters parameters) {
        return true;
    }
}
