package com.media_sanctum.backend.utils.json.matchers.impl;

import com.media_sanctum.backend.utils.json.matchers.JsonValueMatcher;
import com.media_sanctum.backend.utils.json.matchers.MatcherParameters;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class TimestampMatcher implements JsonValueMatcher<String> {

    private static final String PLACEHOLDER = "TIMESTAMP";
    private static final Pattern PATTERN = Pattern.compile("\\{\\{TIMESTAMP(?:\\?amount=(\\d+)&unit=([A-Z_]+))?\\}\\}");

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
        try {
            var parsedTime = LocalDateTime.parse(actual).toInstant(ZoneOffset.UTC);
            var placeholder = parameters.getPlaceholder();

            if ("{{TIMESTAMP}}".equals(placeholder)) {
                return true;
            }

            var matcher = PATTERN.matcher(placeholder);
            if (!matcher.matches()) return false;

            var amount = Long.parseLong(matcher.group(1));
            var unit = ChronoUnit.valueOf(matcher.group(2));
            var skew = Duration.of(amount, unit);
            var now = LocalDateTime.now().toInstant(ZoneOffset.UTC);

            return !parsedTime.isBefore(now.minus(skew)) && !parsedTime.isAfter(now.plus(skew));
        } catch (Exception _) {
            return false;
        }
    }
}