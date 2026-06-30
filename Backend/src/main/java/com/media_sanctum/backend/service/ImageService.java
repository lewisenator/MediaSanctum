package com.media_sanctum.backend.service;

import com.media_sanctum.backend.client.hardcover.model.HardcoverImage;
import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.entity.ImageType;
import com.media_sanctum.backend.repository.ImageRepository;
import com.media_sanctum.backend.resource.ImageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ImageService {

    private static final Pattern SAFE_IMAGE_PATTERN = Pattern.compile("^.*(jpg|jpeg|png|gif|webp)$");

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image upsertImage(String directory, ImageType imageType, HardcoverImage hardcoverImage) {
        var result = findByHardcoverId(hardcoverImage.getId()).orElse(null);
        if (result == null && hardcoverImage.getUrl() != null) {
            var matcher = SAFE_IMAGE_PATTERN.matcher(hardcoverImage.getUrl());
            if (!matcher.matches()) {
                log.error("Invalid image URL: {}", hardcoverImage.getUrl());
                return null;
            }
            var extension = matcher.group(1);
            var filename = imageType.getFileName() + "." + extension;
            var targetPath = Path.of(directory, filename);
            try {
                URL url = URI.create(hardcoverImage.getUrl()).toURL();
                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.error("Failed to download image from URL: {}", hardcoverImage.getUrl(), e);
                return null;
            }
            var image = Image.builder()
                    .color(hardcoverImage.getColor())
                    .extension(extension)
                    .filename(filename)
                    .directory(directory)
                    .width(hardcoverImage.getWidth())
                    .height(hardcoverImage.getHeight())
                    .imageType(imageType)
                    .hardcoverId(hardcoverImage.getId())
                    .build();
            result = saveImage(image);
        }
        return result;
    }

    public Optional<Image> findByHardcoverId(Integer hardcoverId) {
        return imageRepository.findByHardcoverId(hardcoverId);
    }

    public Image getById(String id) {
        return imageRepository.findById(id).orElse(null);
    }

    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public static ImageResponse toResponse(Image image) {
        if (image == null) {
            return null;
        }

        var url = "/public/images/" + image.getId() + "." + image.getExtension();
        return ImageResponse.builder()
                .id(image.getId())
                .url(url)
                .color(image.getColor())
                .width(image.getWidth())
                .height(image.getHeight())
                .build();
    }
}
