package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, String> {

    Optional<Progress> findByBookIdAndUserId(String bookId, String userId);
}
