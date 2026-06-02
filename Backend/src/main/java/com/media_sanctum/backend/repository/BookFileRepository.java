package com.media_sanctum.backend.repository;

import com.media_sanctum.backend.entity.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookFileRepository extends JpaRepository<BookFile, String> {

    List<BookFile> findAllByBook(String bookId);
}
