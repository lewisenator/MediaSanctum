package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

public class AnyFloatMatcher implements JsonValueMatcher<Double> {

    private static final String PLACEHOLDER = "FLOAT";

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<Double> supportedType() {
        return Double.class;
    }

    @Override
    public boolean validate(String path, Double actual, MatcherParameters parameters) {
        return Math.abs(actual) <= Float.MAX_VALUE;
    }
}
