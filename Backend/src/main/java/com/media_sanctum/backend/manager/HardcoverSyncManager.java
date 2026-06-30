package com.media_sanctum.backend.manager;

import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.client.hardcover.model.HardcoverCountry;
import com.media_sanctum.backend.client.hardcover.model.HardcoverEdition;
import com.media_sanctum.backend.client.hardcover.model.HardcoverLanguage;
import com.media_sanctum.backend.entity.Author;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.entity.Edition;
import com.media_sanctum.backend.entity.EditionType;
import com.media_sanctum.backend.entity.ImageType;
import com.media_sanctum.backend.service.AuthorService;
import com.media_sanctum.backend.service.BookService;
import com.media_sanctum.backend.service.EditionService;
import com.media_sanctum.backend.service.ImageService;
import com.media_sanctum.backend.service.MediaStorageService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HardcoverSyncManager {

    private final HardcoverClient hardcoverClient;
    private final BookService bookService;
    private final AuthorService authorService;
    private final EditionService editionService;
    private final ImageService imageService;
    private final MediaStorageService mediaStorageService;

    public HardcoverSyncManager(
            HardcoverClient hardcoverClient,
            BookService bookService,
            AuthorService authorService,
            EditionService editionService,
            ImageService imageService,
            MediaStorageService mediaStorageService
    ) {
        this.hardcoverClient = hardcoverClient;
        this.bookService = bookService;
        this.authorService = authorService;
        this.editionService = editionService;
        this.imageService = imageService;
        this.mediaStorageService = mediaStorageService;
    }

    public Book addBook(Integer hardcoverId) {
        var hardcoverBook = hardcoverClient.getBook(hardcoverId);
        if (hardcoverBook.isNonCanonical()) {
            hardcoverBook = hardcoverClient.getBook(hardcoverBook.getCanonicalId());
        }

        var book = Book.builder()
                .hardcoverId(hardcoverBook.getId())
                .headline(hardcoverBook.getHeadline())
                .title(hardcoverBook.getTitle())
                .slug(hardcoverBook.getSlug())
                .subtitle(hardcoverBook.getSubtitle())
                .description(hardcoverBook.getDescription())
                .releaseYear(hardcoverBook.getReleaseYear())
                .pages(hardcoverBook.getPages())
                .audioSeconds(hardcoverBook.getAudioSeconds())
                .author(addAuthor(hardcoverBook.getAuthorHardcoverId().orElseThrow()))
                .featuredSeries(hardcoverBook.getFeaturedBookSeries())
                .tags(hardcoverBook.getSimpleTags())
                .rating(hardcoverBook.getRating())
                .ratingsCount(hardcoverBook.getRatingsCount())
                .build();

        var existingBook = bookService.getBookByHardcoverId(hardcoverBook.getId());
        if (existingBook != null) {
            book.setId(existingBook.getId());
            book.setCreatedAt(existingBook.getCreatedAt());
        }

        var bookDirectory = mediaStorageService.bookDirectory(book);
        book.setEbookEdition(upsertEdition(bookDirectory, hardcoverBook.getDefaultCoverEdition(),
                EditionType.EBOOK, ImageType.EBOOK));
        book.setAudiobookEdition(upsertEdition(bookDirectory, hardcoverBook.getDefaultAudioEdition(),
                EditionType.AUDIOBOOK, ImageType.AUDIOBOOK));

        return bookService.saveBook(book);
    }

    public Author addAuthor(Integer hardcoverId) {
        return authorService.getAuthorByHardcoverId(hardcoverId).orElseGet(() -> {
            var hardcoverAuthor = hardcoverClient.getAuthor(hardcoverId);
            if (hardcoverAuthor.isNonCanonical()) {
                hardcoverAuthor = hardcoverClient.getAuthor(hardcoverAuthor.getCanonicalId());
            }
            var authorDir = mediaStorageService.authorDirectory(hardcoverAuthor.getName());
            var image = imageService.upsertImage(authorDir, ImageType.MUGSHOT, hardcoverAuthor.getCachedImage());
            return authorService.addAuthor(hardcoverAuthor, image);
        });
    }

    public Edition upsertEdition(String bookDirectory, HardcoverEdition hardcoverEdition,
                                  EditionType editionType, ImageType imageType) {
        if (hardcoverEdition == null || hardcoverEdition.getId() == null) {
            return null;
        }

        var hardcoverId = hardcoverEdition.getId();
        var result = editionService.getEditionByHardcoverId(hardcoverId);
        if (result == null) {
            var edition = Edition.builder()
                    .hardcoverId(hardcoverId)
                    .asin(hardcoverEdition.getAsin())
                    .isbn10(hardcoverEdition.getIsbn10())
                    .isbn13(hardcoverEdition.getIsbn13())
                    .language(Optional.ofNullable(hardcoverEdition.getLanguage())
                            .map(HardcoverLanguage::getCode2)
                            .orElse(null))
                    .country(Optional.ofNullable(hardcoverEdition.getCountry())
                            .map(HardcoverCountry::getCode2)
                            .orElse(null))
                    .editionType(editionType)
                    .build();

            edition.setImage(imageService.upsertImage(bookDirectory, imageType, hardcoverEdition.getCachedImage()));

            switch (editionType) {
                case EBOOK -> edition.setPages(hardcoverEdition.getPages());
                case AUDIOBOOK -> edition.setAudioSeconds(hardcoverEdition.getAudioSeconds());
                default -> throw new IllegalStateException("Unexpected value: " + editionType);
            }

            result = editionService.saveEdition(edition);
        }
        return result;
    }
}
