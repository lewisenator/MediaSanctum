package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, String> {

    Optional<Image> findByHardcoverId(Integer hardcoverId);
}
