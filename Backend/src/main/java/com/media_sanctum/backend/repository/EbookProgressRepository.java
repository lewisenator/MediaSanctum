package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.EbookProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EbookProgressRepository extends JpaRepository<EbookProgress, String> {

    Optional<EbookProgress> findByBookIdAndUserId(String bookId, String userId);
}
