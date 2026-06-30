package com.media_sanctum.backend.utils.json.matchers;

import com.media_sanctum.backend.utils.json.matchers.impl.AnyBooleanMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.AnyFloatMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.AnyIntegerMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.AnyObjectMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.AnyStringMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.AnyUUIDMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.IgnoreMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.ObjectArrayMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.StringArrayMatcher;
import com.media_sanctum.backend.utils.json.matchers.impl.TimestampMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Registry for JSON value matchers used in contract testing.
 *
 * This registry maintains a collection of matchers and provides methods to find
 * the appropriate matcher for a given placeholder and validate values.
 *
 * To add a new custom matcher:
 * 1. Create a class that implements JsonValueMatcher
 * 2. Register it using MatcherRegistry.register(YourMatcher())
 */
public class MatcherRegistry {

    private static MatcherRegistry instance;

    private final List<JsonValueMatcher> matchers = new ArrayList<>();

    private MatcherRegistry() {
        registerDefaults();
    }

    /**
     * Finds the appropriate matcher for the given placeholder.
     *
     * @param placeholder The placeholder string from the JSON contract
     * @return Optional<JsonValueMatcher> The first matcher that matches the placeholder, or Optional.empty() if none found
     */
    public Optional<JsonValueMatcher> findMatcher(String placeholder) {
        return matchers.stream()
                .filter(matcher -> matcher.matches(placeholder))
                .findFirst();
    }

    /**
     * Validates the actual value against the placeholder using the appropriate matcher.
     *
     * @param actual The actual value from the JSON response
     * @param placeholder The placeholder string from the expected JSON
     * @return true if a matcher was found and validation passed, false otherwise
     */
    public boolean validate(String path, Object actual, String placeholder) {
        var matcher = findMatcher(placeholder).orElse(null);
        if (matcher == null) {
            return false;
        }
        var parameters = MatcherParameters.from(placeholder);
        return matcher.validateUnchecked(path, actual, parameters);
    }

    public MatcherRegistry clear() {
        matchers.clear();
        return this;
    }

    public List<JsonValueMatcher> matchers() {
        return new ArrayList<>(matchers);
    }

    /**
     * Resets the registry to its default state.
     * Useful for testing or clearing custom matchers.
     */
    public MatcherRegistry reset() {
        matchers.clear();
        registerDefaults();
        return this;
    }

    /**
     * Registers a custom matcher.
     * The matcher will be checked in the order it was registered.
     *
     * @param matcher The matcher to register
     */
    public MatcherRegistry register(JsonValueMatcher matcher) {
        matchers.add(matcher);
        return this;
    }

    /**
     * Registers all built-in matchers.
     * This is called automatically during initialization.
     */
    private void registerDefaults() {
        register(new IgnoreMatcher());
        register(new AnyStringMatcher());
        register(new AnyBooleanMatcher());
        register(new AnyIntegerMatcher());
        register(new TimestampMatcher());
        register(new AnyUUIDMatcher());
        register(new AnyFloatMatcher());
        register(new StringArrayMatcher());
        register(new AnyObjectMatcher());
        register(new ObjectArrayMatcher());
    }

    public static MatcherRegistry getInstance() {
        if (instance == null) {
            instance = new MatcherRegistry();
        }
        return instance;
    }
}
