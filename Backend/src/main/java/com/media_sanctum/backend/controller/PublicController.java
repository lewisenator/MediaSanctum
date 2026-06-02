package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.entity.BookFile;
import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.service.BookFileService;
import com.media_sanctum.backend.service.ImageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/public")
public class PublicController {

    private static final Pattern IMAGE_NAME = Pattern.compile("^([^.]*).(jpg|jpeg|png|gif|webp)$");
    private static final Pattern BOOK_FILE_NAME = Pattern.compile("^([^.]*).(mobi|epub)$");

    private final ImageService imageService;
    private final BookFileService bookFileService;

    public PublicController(ImageService imageService, BookFileService bookFileService) {
        this.imageService = imageService;
        this.bookFileService = bookFileService;
    }

    @GetMapping(
            value = "/book-files/{bookFileName}",
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

        var bookFileId = matcher.group(1);
        var extension = matcher.group(2);
        var bookFile = bookFileService.getBookFile(bookFileId);

        File file = getBookFile(bookFile);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bookFile.getContentType()))
                .body(new FileSystemResource(file));
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
}
