package com.media_sanctum.backend.entity;

public enum ImageType {
    MUGSHOT("mugshot"),
    AUDIOBOOK("audiobook"),
    EBOOK("cover");

    private String fileName;

    ImageType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
