package com.media_sanctum.backend.utils.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.utils.json.matchers.MatcherRegistry;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class JsonAssertionBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern placeholderPattern = Pattern.compile("(\\{\\{[A-Z0-9:-]+\\}\\})");

    private final String responseBody;

    public JsonAssertionBuilder(String responseBody) {
        this.responseBody = responseBody;
    }

    public static JsonAssertionBuilder assertThatJson(String responseBody) {
        return new JsonAssertionBuilder(responseBody);
    }

    public void matchesContract(String contract) {
        try {
            var customizations = findCustomizations(contract);
            if (customizations.isEmpty()) {
                JSONAssert.assertEquals(contract, responseBody, true);
            } else {
                var comparator = new CustomComparator(JSONCompareMode.STRICT, customizations.toArray(new Customization[customizations.size()]));
                JSONAssert.assertEquals(contract, responseBody, comparator);
            }
        } catch (Exception|AssertionError e) {
            String message = String.format("""
                        JSON Contract Mismatch
                            Expected: \n\n%s
                            \nActual: \n\n%s
                            \nError: %s
                        """, pretty(contract), pretty(responseBody), e.getMessage());

            throw new AssertionError(message);
        }
    }

    private static String pretty(String json) {
        String result = json;
        try {
            Object jsonObject = OBJECT_MAPPER.readValue(json, Object.class);
            result = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (Exception e) {
            // intentionally swallow
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static List<Customization> findCustomizations(String json) {
        List<Customization> customizations = new ArrayList<>();

        try {
            var jsonObject = OBJECT_MAPPER.readTree(json);
            if (jsonObject.isObject()) {
                customizations.addAll(findCustomizations(new StringJoiner("."), jsonObject));
            }
        } catch (Exception e) {
            throw new RuntimeException("Problem parsing json contract: ", e);
        }

        return customizations;
    }

    private static List<Customization> findCustomizations(StringJoiner root, JsonNode json) {
        List<Customization> customizations = new ArrayList<>();
        if (json.isObject()) {
            json.forEachEntry((key, value) -> {
                if (value.isTextual()) {
                    var matcher = placeholderPattern.matcher(value.asText());
                    while (matcher.find()) {
                        String placeholder = matcher.group(1);
                        var path = new StringJoiner(".").merge(root).add(key).toString();
                        customizations.add(new Customization(path, (a, b) -> {
                            // Delegate to MatcherRegistry for validation
                            return MatcherRegistry.getInstance().validate(a, placeholder);
                        }));
                    }
                } else if (value.isObject()) {
                    customizations.addAll(findCustomizations(new StringJoiner(".").merge(root).add(key), value));
                }
            });
        }
        return customizations;
    }
}
