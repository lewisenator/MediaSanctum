package com.media_sanctum.backend.utils.json.matchers;

import org.json.JSONObject;

/**
 * Interface for custom JSON value matchers used in contract testing.
 *
 * Implementations can provide custom validation logic for dynamic values
 * in JSON responses (e.g., timestamps, UUIDs, generated IDs).
 */
public interface JsonValueMatcher<T> {

    String PREFIX = "{{";
    String SUFFIX = "}}";

    String getName();

    default boolean matches(String placeholder) {
        var result = false;
        var name = getName();
        if (placeholder != null) {
            result = placeholder.startsWith(PREFIX + name + "?")
                    || placeholder.startsWith(PREFIX + name + SUFFIX);
        }
        return result;
    }

    Class<T> supportedType();

    boolean validate(String path, T actual, MatcherParameters parameters);

    default boolean validateUnchecked(String path, Object actual, MatcherParameters parameters) {
        if (JSONObject.NULL.equals(actual) && parameters.isNullable()) {
            return true;
        }
        if (!supportedType().isInstance(actual)) {
            return false;
        }
        return validate(path, supportedType().cast(actual), parameters);
    }
}
