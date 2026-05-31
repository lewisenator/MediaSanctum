package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.Author;
import com.media_sanctum.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Author> findByHardcoverId(Integer hardcoverId);
}
