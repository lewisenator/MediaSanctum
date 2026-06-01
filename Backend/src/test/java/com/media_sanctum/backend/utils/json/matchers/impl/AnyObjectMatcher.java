package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;
import org.json.JSONObject;

public class AnyObjectMatcher implements JsonValueMatcher<JSONObject> {

    private static final String PLACEHOLDER = "OBJECT";

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<JSONObject> supportedType() {
        return JSONObject.class;
    }

    @Override
    public boolean validate(String path, JSONObject actual, MatcherParameters parameters) {
        return true;
    }
}
