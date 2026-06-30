package com.media_sanctum.backend.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EditionType {
    AUDIOBOOK("audiobook"),
    EBOOK("ebook");

    final String pathValue;

    EditionType(String pathValue) {
        this.pathValue = pathValue;
    }

    public static EditionType fromPathValue(String value) {
        return Arrays.stream(values())
                .filter(e -> e.pathValue.equals(value))
                .findFirst()
                .orElse(null);
    }
}
