package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.repository.BookRepository;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.TagResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return bookRepository.findById(id).orElse(null);
    }

    public Book getBookByHardcoverId(Integer id) {
        return bookRepository.findByHardcoverId(id).orElse(null);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public boolean exists(String id) {
        return bookRepository.existsById(id);
    }

    public static BookResponse toResponse(Book book) {
        if (book == null) {
            return null;
        }
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
                .rating(book.getRating())
                .ratingsCount(book.getRatingsCount())
                .tags(Optional.ofNullable(book.getTags())
                        .orElse(List.of())
                        .stream()
                        .map(item -> TagResponse.builder()
                            .tag(item.getTag())
                            .count(item.getCount())
                            .category(item.getTagCategory().getCategory())
                            .build())
                        .toList())
                .createdAt(book.getCreatedAt().toString())
                .updatedAt(book.getUpdatedAt().toString())
                .author(AuthorService.toResponse(book.getAuthor()))
                .ebookEdition(EditionService.toResponse(book.getEbookEdition()))
                .audiobookEdition(EditionService.toResponse(book.getAudiobookEdition()))
                .featuredSeries(book.getFeaturedSeries())
                .ebookFile(BookFileService.toBookFileResponse(book.getEbookFile()))
                .audiobookFile(BookFileService.toBookFileResponse(book.getAudiobookFile()))
                .build();
    }
}
