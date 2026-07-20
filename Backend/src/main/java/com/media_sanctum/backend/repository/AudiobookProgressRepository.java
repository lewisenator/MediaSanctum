package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.AudiobookProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AudiobookProgressRepository extends JpaRepository<AudiobookProgress, String> {

    Optional<AudiobookProgress> findFirstByBookIdAndUserId(String bookId, String userId);
}
