package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ObjectArrayMatcher implements JsonValueMatcher<JSONArray> {

    private static final String PLACEHOLDER = "OBJECT-ARRAY";
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
                if (!(item instanceof JSONObject)) {
                    result = false;
                    break;
                }
                var maybeContract = parameters.getString(MatcherParameters.PARAM_CONTRACT);
                if (maybeContract.isPresent()) {
                    var contractValue = maybeContract.get();
                    var contractJson = URLDecoder.decode(contractValue, StandardCharsets.UTF_8);
                    JsonAssertionBuilder.assertThatJson(item.toString())
                            .withBasePath(path + String.format("[%d]", i))
                            .matchesContract(contractJson);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
