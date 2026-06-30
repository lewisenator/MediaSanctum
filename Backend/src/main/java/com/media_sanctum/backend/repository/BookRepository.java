package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Book> findByHardcoverId(Integer hardcoverId);
}
