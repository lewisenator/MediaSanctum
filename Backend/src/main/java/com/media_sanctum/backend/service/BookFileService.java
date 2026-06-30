package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.BookFile;
import com.media_sanctum.backend.repository.BookFileRepository;
import com.media_sanctum.backend.resource.BookFileResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class BookFileService {

    private final BookFileRepository bookFileRepository;

    public BookFileService(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    public BookFile getBookFile(String bookId) {
        return bookFileRepository.findById(bookId).orElse(null);
    }

    public List<BookFile> getBookFiles() {
        return bookFileRepository.findAll();
    }

    public BookFile saveBookFile(BookFile bookFile) {
        return bookFileRepository.save(bookFile);
    }

    public static String getFileMD5(String filePath) {
        String result = null;
        try (InputStream fis = Files.newInputStream(Path.of(filePath))) {
            result = DigestUtils.md5Hex(fis);
        } catch (IOException _) {} // NOPMD
        return result;
    }

    public static BookFileResponse toBookFileResponse(BookFile bookFile) {
        if (bookFile == null) {
            return null;
        }

        return BookFileResponse.builder()
                .id(bookFile.getId())
                .size(bookFile.getSize())
                .url(bookFile.getUrl())
                .filename(bookFile.getFilename())
                .contentType(bookFile.getContentType())
                .editionType(bookFile.getEditionType())
                .ffProbe(bookFile.getFfprobe())
                .createdAt(bookFile.getCreatedAt().toString())
                .updatedAt(bookFile.getUpdatedAt().toString())
                .build();
    }
}
