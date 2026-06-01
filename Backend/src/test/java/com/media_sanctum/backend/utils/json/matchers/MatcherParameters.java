package com.media_sanctum.backend.utils.json.matchers;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class MatcherParameters {

    private static final String PARAMS_REGEX = "\\{\\{[^?]+(\\?[^}]*)\\}\\}";
    private static final Pattern PARAMS_PATTERN = Pattern.compile(PARAMS_REGEX);

    public static final String PARAM_NULLABLE = "nullable";
    public static final String PARAM_ALLOW_EMPTY = "allowEmpty";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_UNIT = "unit";
    public static final String PARAM_CONTRACT = "contract";

    private static final Set<String> ALLOWED_PARAMS = Set.of(
            PARAM_NULLABLE,
            PARAM_ALLOW_EMPTY,
            PARAM_AMOUNT,
            PARAM_UNIT,
            PARAM_CONTRACT
    );

    private final String placeholder;
    private Map<String, String> parameters = Map.of();

    private MatcherParameters(String placeholder) {
        this.placeholder = placeholder;
        processParameters();
    }

    private void processParameters() {
        var result = new HashMap<String, String>();
        var matcher = PARAMS_PATTERN.matcher(placeholder);
        if (matcher.matches()) {
            var queryString = matcher.group(1);
            UriComponentsBuilder.fromUriString(queryString).build().getQueryParams().forEach((key, values) -> {
                if (!values.isEmpty()) {
                    if (!ALLOWED_PARAMS.contains(key)) {
                        throw new RuntimeException("Matcher param " + key + " is not allowed.");
                    }
                    result.put(key, values.getFirst());
                }
            });
        }
        parameters = result;
    }

    public Optional<Boolean> getBoolean(String key) {
        return Optional.ofNullable(parameters.get(key)).map(Boolean::parseBoolean);
    }

    public Optional<String> getString(String key) {
        return Optional.ofNullable(parameters.get(key));
    }


    public boolean isNullable() {
        var result = false;
        if (parameters.containsKey(PARAM_NULLABLE)) {
            result = Boolean.parseBoolean(parameters.get(PARAM_NULLABLE));
        }
        return result;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public static MatcherParameters from(String placeholder) {
        return new MatcherParameters(placeholder);
    }
}
