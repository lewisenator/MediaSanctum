package com.media_sanctum.backend.controller;

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

    private final ImageService imageService;

    public PublicController(ImageService imageService) {
        this.imageService = imageService;
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
        File file = Path.of(image.getDirectory(), image.getFilename()).toFile();
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = switch(extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "webp" -> "image/webp";
            default -> throw new IllegalStateException("Unexpected value: " + extension);
        };
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(new FileSystemResource(file));
    }
}
