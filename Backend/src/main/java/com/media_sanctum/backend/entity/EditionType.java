package com.media_sanctum.backend.entity;

import java.util.Arrays;

public enum EditionType {
    AUDIOBOOK("audiobook"),
    EBOOK("ebook");

    final String pathValue;

    EditionType(String pathValue) {
        this.pathValue = pathValue;
    }

    public static EditionType fromPathValue(String value) {
        return Arrays.stream(EditionType.values())
                .filter(e -> e.pathValue.equals(value))
                .findFirst()
                .orElse(null);
    }
}
