package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;
import org.json.JSONArray;
import org.json.JSONException;

public class StringArrayMatcher implements JsonValueMatcher<JSONArray> {

    private static final String PLACEHOLDER = "STRING-ARRAY";
    private static final Boolean DEFAULT_ALLOW_EMPTY = true;

    @Override
    public String getName() {
        return PLACEHOLDER;
    }

    @Override
    public Class<JSONArray> supportedType() {
        return JSONArray.class;
    }

    @Override
    public boolean validate(String path, JSONArray actual, MatcherParameters parameters) {
        var result = true;

        var allowEmpty = parameters.getBoolean(MatcherParameters.PARAM_ALLOW_EMPTY).orElse(DEFAULT_ALLOW_EMPTY);
        var length = actual.length();
        if (!allowEmpty && length == 0) {
            return false;
        }

        try {
            for (int i = 0; i < length; i++) {
                var item = actual.get(i);
                if (!(item instanceof String)) {
                    result = false;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
