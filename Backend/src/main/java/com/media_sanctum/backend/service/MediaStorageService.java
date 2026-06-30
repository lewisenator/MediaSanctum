package com.media_sanctum.backend.service;

import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class MediaStorageService {

    private final MediaSanctumConfig mediaSanctumConfig;

    public MediaStorageService(MediaSanctumConfig mediaSanctumConfig) {
        this.mediaSanctumConfig = mediaSanctumConfig;
    }

    public String authorDirectory(String authorName) {
        return createDirectory(
                Path.of(mediaSanctumConfig.dataDir(), "ebooks", authorName).toString()
        );
    }

    public String bookDirectory(Book book) {
        var authorDir = authorDirectory(book.getAuthor().getName());
        return createDirectory(Path.of(authorDir, book.getTitle()).toString());
    }

    public String audiobookDirectory(Book book) {
        return createDirectory(Path.of(mediaSanctumConfig.dataDir(), "audiobooks",
                book.getAuthor().getName(), book.getTitle()).toString());
    }

    private String createDirectory(String directory) {
        try {
            Files.createDirectories(Path.of(directory));
            return directory;
        } catch (IOException e) {
            log.error("Failed to create directory: {}", directory, e);
            throw new UncheckedIOException(e);
        }
    }
}
