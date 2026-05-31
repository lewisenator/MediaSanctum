package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.repository.ImageRepository;
import com.media_sanctum.backend.resource.ImageResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
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
        if (image == null) return null;

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
