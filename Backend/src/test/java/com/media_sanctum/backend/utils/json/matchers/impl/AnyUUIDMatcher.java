package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

import java.util.regex.Pattern;

public class AnyUUIDMatcher implements JsonValueMatcher<String> {

    private static final String PLACEHOLDER = "UUID";
    // Regex for a standard 36-character UUID (8-4-4-4-12 digits)
    private static final String REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final Pattern UUID_REGEX = Pattern.compile(REGEX);


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
        return UUID_REGEX.matcher(actual).matches();
    }
}
