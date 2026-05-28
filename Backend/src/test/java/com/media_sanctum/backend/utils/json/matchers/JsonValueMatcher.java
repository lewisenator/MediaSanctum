package com.media_sanctum.backend.utils.json.matchers;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for custom JSON value matchers used in contract testing.
 *
 * Implementations can provide custom validation logic for dynamic values
 * in JSON responses (e.g., timestamps, UUIDs, generated IDs).
 */
public interface JsonValueMatcher {

    /**
     * Determines if this matcher can handle the given placeholder.
     *
     * @param placeholder The placeholder string from the JSON contract
     * @return true if this matcher can validate values for this placeholder
     */
    boolean matches(String placeholder);

    /**
     * Validates that the actual value from the JSON response matches the expected criteria.
     *
     * @param actual The actual value from the JSON response
     * @param placeholder The placeholder string from the expected JSON
     * @return true if the actual value is valid for this placeholder, false otherwise
     */
    boolean validate(Object actual, String placeholder);
}
