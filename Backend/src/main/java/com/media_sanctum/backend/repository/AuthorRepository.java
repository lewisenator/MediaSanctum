package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, String> {

    Optional<Author> findByHardcoverId(Integer hardcoverId);
}
