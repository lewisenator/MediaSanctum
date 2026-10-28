package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.EbookProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface EbookProgressRepository extends JpaRepository<EbookProgress, String> {

    Optional<EbookProgress> findByBookIdAndUserId(String bookId, String userId);

    List<EbookProgress> findAllByBookId(String bookId);
}
