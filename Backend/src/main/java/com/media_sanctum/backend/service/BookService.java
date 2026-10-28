package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.repository.AudiobookProgressRepository;
import com.media_sanctum.backend.repository.BookFileRepository;
import com.media_sanctum.backend.repository.BookRepository;
import com.media_sanctum.backend.repository.EbookProgressRepository;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.TagResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookFileRepository bookFileRepository;
    private final EbookProgressRepository ebookProgressRepository;
    private final AudiobookProgressRepository audiobookProgressRepository;

    public BookService(
            BookRepository bookRepository,
            BookFileRepository bookFileRepository,
            EbookProgressRepository ebookProgressRepository,
            AudiobookProgressRepository audiobookProgressRepository
    ) {
        this.bookRepository = bookRepository;
        this.bookFileRepository = bookFileRepository;
        this.ebookProgressRepository = ebookProgressRepository;
        this.audiobookProgressRepository = audiobookProgressRepository;
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

    /**
     * Deletes a book together with its files and reading progress rows.
     *
     * @return false when no book has this id
     */
    @Transactional
    public boolean deleteBook(String id) {
        var book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return false;
        }
        removeBook(book);
        return true;
    }

    /** Deletes every book of an author; used before the author row itself is removed. */
    @Transactional
    public void deleteBooksByAuthorId(String authorId) {
        bookRepository.findAllByAuthorId(authorId).forEach(this::removeBook);
    }

    private void removeBook(Book book) {
        // books.*_file_id and book_files.book_id reference each other, so break the cycle first.
        book.setEbookFile(null);
        book.setAudiobookFile(null);
        bookRepository.saveAndFlush(book);
        bookFileRepository.deleteAll(bookFileRepository.findAllByBookId(book.getId()));
        ebookProgressRepository.deleteAll(ebookProgressRepository.findAllByBookId(book.getId()));
        audiobookProgressRepository.deleteAll(audiobookProgressRepository.findAllByBookId(book.getId()));
        bookRepository.delete(book);
        bookRepository.flush();
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
