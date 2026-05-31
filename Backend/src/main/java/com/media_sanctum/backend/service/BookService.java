package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.repository.BookRepository;
import com.media_sanctum.backend.resource.BookResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponse> getBooksResponse() {
        return getBooks().stream().map(BookService::toResponse).toList();
    }

    public List<Book> getBooks() {
        var sortByCreatedAt = Sort.by("createdAt").descending();
        var pageRequest = PageRequest.of(0, 100, sortByCreatedAt);
        var entities = bookRepository.findAll(pageRequest);
        return entities.stream().toList();
    }

    public BookResponse getBookResponse(String id) {
        var book = getBook(id);
        return toResponse(book);
    }

    public Book getBook(String id) {
        return bookRepository.findById(id).orElseThrow();
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public static BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .headline(book.getHeadline())
                .title(book.getTitle())
                .slug(book.getSlug())
                .subtitle(book.getSubtitle())
                .description(book.getDescription())
                .releaseYear(book.getReleaseYear())
                .pages(book.getPages())
                .audioSeconds(book.getAudioSeconds())
                .createdAt(book.getCreatedAt().toString())
                .updatedAt(book.getUpdatedAt().toString())
                .author(AuthorService.toResponse(book.getAuthor()))
                .build();
    }
}
