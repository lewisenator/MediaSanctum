package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.Edition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EditionRepository extends JpaRepository<Edition, String> {

    Optional<Edition> findByHardcoverId(Integer hardcoverId);
}