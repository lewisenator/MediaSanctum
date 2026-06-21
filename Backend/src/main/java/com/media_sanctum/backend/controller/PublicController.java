package com.media_sanctum.backend.controller;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.media_sanctum.backend.entity.BookFile;
import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.service.BookFileService;
import com.media_sanctum.backend.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/public")
public class PublicController {

    private static final Pattern IMAGE_NAME = Pattern.compile("^([^.]*).(jpg|jpeg|png|gif|webp)$");
    private static final Pattern BOOK_FILE_NAME = Pattern.compile("^([^.]*).(mobi|epub)$");
    private static final Pattern AUDIO_FILE_NAME = Pattern.compile("^([^.]*).(m4b)$");

    private final ImageService imageService;
    private final BookFileService bookFileService;

    public PublicController(ImageService imageService, BookFileService bookFileService) {
        this.imageService = imageService;
        this.bookFileService = bookFileService;
    }

    @GetMapping(
            value = "/ebook-files/{bookFileName}",
            produces = {
                    "application/epub+zip",
                    "application/x-mobipocket-ebook"
            }
    )
    public ResponseEntity<?> getBookFile(@PathVariable String bookFileName) {
        var matcher = BOOK_FILE_NAME.matcher(bookFileName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid bookFile: " + bookFileName);
        }

        var maybeFile = loadFile(matcher);
        if (maybeFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var file = maybeFile.get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new FileSystemResource(file.getFile()));
    }

    @GetMapping("/audiobook-files/{bookFileName}")
    public ResponseEntity<ResourceRegion> streamAudio(
            @PathVariable String bookFileName,
            @RequestHeader HttpHeaders headers
    ) throws IOException {
        var matcher = AUDIO_FILE_NAME.matcher(bookFileName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid audio file: " + bookFileName);
        }
        var maybeFile = loadFile(matcher);
        if (maybeFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var file = maybeFile.get();

        Resource resource = new FileSystemResource(file.getFile());
        long contentLength = resource.contentLength();
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().getFirst();
        ResourceRegion region = (range != null)
                ? range.toResourceRegion(resource)
                : new ResourceRegion(resource, 0, contentLength);
        return ResponseEntity
                .status(range != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .contentType(MediaType.parseMediaType("audio/mp4"))
                .body(region);
    }

    @GetMapping(value = "/images/{imageName}", produces = {
            MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, "image/webp"
    })
    public ResponseEntity<?> getImage(@PathVariable String imageName) {
        var matcher = IMAGE_NAME.matcher(imageName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid image: " + imageName);
        }

        var imageId = matcher.group(1);
        var extension = matcher.group(2);
        var image = imageService.getById(imageId);

        String contentType = getContentTypeFromExtension(extension);
        File file = getImageFile(image);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(new FileSystemResource(file));
    }

    private static File getBookFile(BookFile bookFile) {
        File file = Path.of(bookFile.getDirectory(), bookFile.getFilename()).toFile();
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    private static File getImageFile(Image image) {
        File file = Path.of(image.getDirectory(), image.getFilename()).toFile();
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    private static String getContentTypeFromExtension(String extension) {
        return switch(extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "webp" -> "image/webp";
            default -> throw new IllegalStateException("Unexpected value: " + extension);
        };
    }
    private Optional<LoadedFile> loadFile(Matcher filenameMatcher) {
        var result = Optional.<LoadedFile>empty();
        var bookFileId = filenameMatcher.group(1);
        var extension = filenameMatcher.group(2);
        var bookFile = bookFileService.getBookFile(bookFileId);

        File file = getBookFile(bookFile);
        if (file == null) {
            return result;
        }
        return Optional.of(LoadedFile.builder()
                .file(file)
                .contentType(bookFile.getContentType())
                .extension(extension)
                .build());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LoadedFile {
        private File file;
        private String contentType;
        private String extension;
    }
}
