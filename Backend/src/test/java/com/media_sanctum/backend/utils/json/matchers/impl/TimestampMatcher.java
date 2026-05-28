package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class TimestampMatcher implements JsonValueMatcher {

    private static final Pattern PATTERN = Pattern.compile("\\{\\{TIMESTAMP(?::(\\d+):([A-Z_]+))?\\}\\}");

    @Override
    public boolean matches(String placeholder) {
        return PATTERN.matcher(placeholder).matches();
    }

    @Override
    public boolean validate(Object actual, String placeholder) {
        if (!(actual instanceof String actualString)) return false;

        try {
            var parsedTime = Instant.parse(actualString);

            if ("{{TIMESTAMP}}".equals(placeholder)) {
                return true;
            }

            var matcher = PATTERN.matcher(placeholder);
            if (!matcher.matches()) return false;

            var amount = Long.parseLong(matcher.group(1));
            var unit = ChronoUnit.valueOf(matcher.group(2));
            var skew = Duration.of(amount, unit);
            var now = Instant.now();

            return !parsedTime.isBefore(now.minus(skew)) && !parsedTime.isAfter(now.plus(skew));
        } catch (Exception _) {
            return false;
        }
    }
}