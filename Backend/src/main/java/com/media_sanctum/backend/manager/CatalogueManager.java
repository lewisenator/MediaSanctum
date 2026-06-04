package com.media_sanctum.backend.manager;

import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.client.hardcover.model.HardcoverCountry;
import com.media_sanctum.backend.client.hardcover.model.HardcoverEdition;
import com.media_sanctum.backend.client.hardcover.model.HardcoverImage;
import com.media_sanctum.backend.client.hardcover.model.HardcoverLanguage;
import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.entity.Author;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.entity.BookFile;
import com.media_sanctum.backend.entity.Edition;
import com.media_sanctum.backend.entity.EditionType;
import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.entity.ImageType;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.service.AuthorService;
import com.media_sanctum.backend.service.BookFileService;
import com.media_sanctum.backend.service.BookService;
import com.media_sanctum.backend.service.EditionService;
import com.media_sanctum.backend.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CatalogueManager {

    private static final Pattern SAFE_IMAGE_PATTERN = Pattern.compile("^.*(jpg|jpeg|png|gif|webp)$");
    private static final Pattern SAVE_BOOK_FILE_PATTERN = Pattern.compile("^.*(pdf|epub|mobi)$");

    private final BookService bookService;
    private final BookFileService bookFileService;
    private final AuthorService authorService;
    private final ImageService imageService;
    private final EditionService editionService;
    private final HardcoverClient hardcoverClient;
    private final MediaSanctumConfig mediaSanctumConfig;

    public CatalogueManager(
            BookService bookService,
            BookFileService bookFileService,
            AuthorService authorService,
            ImageService imageService,
            EditionService editionService,
            HardcoverClient hardcoverClient,
            MediaSanctumConfig mediaSanctumConfig
    ) {
        this.bookService = bookService;
        this.bookFileService = bookFileService;
        this.authorService = authorService;
        this.imageService = imageService;
        this.editionService = editionService;
        this.hardcoverClient = hardcoverClient;
        this.mediaSanctumConfig = mediaSanctumConfig;
    }

    public BookResponse uploadBookFile(
            String bookId,
            EditionType editionType,
            MultipartFile file
    ) {
        var book = bookService.getBook(bookId);
        var uploadDirectory = switch (editionType) {
            case EBOOK -> bookDirectory(book);
            case AUDIOBOOK -> audiobookDirectory(book);
        };

        var fileName = file.getOriginalFilename();
        var matcher = SAVE_BOOK_FILE_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            var message = String.format("Invalid book file extension: %s", fileName);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        var extension = matcher.group(1);
        var desiredFilename = book.getTitle() + "." + extension;

        try {
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, Path.of(uploadDirectory, desiredFilename), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error("Failed to upload {} file: {}", editionType, fileName, e);
            return null;
        }

        var id = UUID.randomUUID().toString();
        var url = ("/public/book-files/%s.%s").formatted(id, extension);
        var bookFile = BookFile.builder()
                .id(id)
                .url(url)
                .size(file.getSize())
                .hash("bob")
                .directory(uploadDirectory)
                .filename(desiredFilename)
                .contentType(file.getContentType())
                .extension(extension)
                .editionType(editionType)
                .book(book)
                .build();

        bookFile = bookFileService.saveBookFile(bookFile);

        switch (editionType) {
            case EBOOK -> book.setEbookFile(bookFile);
            case AUDIOBOOK -> book.setAudiobookFile(bookFile);
        }

        book = bookService.saveBook(book);

        return BookService.toResponse(book);
    }

    public BookResponse addBook(Integer hardcoverId) {
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
                .author(upsertAuthor(hardcoverBook.getAuthorHardcoverId().orElseThrow()))
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

        var bookDirectory = bookDirectory(book);
        createDirectory(bookDirectory);
        book.setEbookEdition(upsertEdition(bookDirectory, hardcoverBook.getDefaultCoverEdition(),
                EditionType.EBOOK, ImageType.EBOOK));
        book.setAudiobookEdition(upsertEdition(bookDirectory, hardcoverBook.getDefaultAudioEdition(),
                EditionType.AUDIOBOOK, ImageType.AUDIOBOOK));

        var savedBook = bookService.saveBook(book);

        return BookService.toResponse(savedBook);
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

            edition.setImage(upsertImage(bookDirectory, imageType, hardcoverEdition.getCachedImage()));

            switch (editionType) {
                case EBOOK:
                    edition.setPages(hardcoverEdition.getPages());
                case AUDIOBOOK:
                    edition.setAudioSeconds(hardcoverEdition.getAudioSeconds());
            }

            result = editionService.saveEdition(edition);
        }
        return result;
    }

    public Author upsertAuthor(Integer hardcoverId) {
        var author = authorService.getAuthorByHardcoverId(hardcoverId).orElse(null);
        if (author == null) {
            author = addAuthor(hardcoverId);
        }
        return author;
    }

    public Author addAuthor(Integer hardcoverId) {
        var hardcoverAuthor = hardcoverClient.getAuthor(hardcoverId);
        if (hardcoverAuthor.isNonCanonical()) {
            hardcoverAuthor = hardcoverClient.getAuthor(hardcoverAuthor.getCanonicalId());
        }

        var author = Author.builder()
                .hardcoverId(hardcoverAuthor.getId())
                .name(hardcoverAuthor.getName())
                .title(hardcoverAuthor.getTitle())
                .alternateNames(hardcoverAuthor.getAlternateNames())
                .slug(hardcoverAuthor.getSlug())
                .bio(hardcoverAuthor.getBio())
                .bornYear(hardcoverAuthor.getBornYear())
                .deathYear(hardcoverAuthor.getDeathYear())
                .booksCount(hardcoverAuthor.getBooksCount())
                .links(hardcoverAuthor.getLinks())
                .build();

        var authorDirectory = authorDirectory(author);
        author.setImage(upsertImage(authorDirectory, ImageType.MUGSHOT, hardcoverAuthor.getCachedImage()));

        return authorService.saveAuthor(author);
    }

    private Image upsertImage(String directory, ImageType imageType, HardcoverImage hardcoverImage) {
        var result = imageService.findByHardcoverId(hardcoverImage.getId()).orElse(null);
        if (result == null && hardcoverImage.getUrl() != null) {

            var matcher = SAFE_IMAGE_PATTERN.matcher(hardcoverImage.getUrl());
            if (!matcher.matches()) {
                log.error("Invalid image URL: {}", hardcoverImage.getUrl());
                return null;
            }
            var extension = matcher.group(1);
            var filename = imageType.getFileName() + "." + extension;
            var targetPath = Path.of(directory, filename);

            try {
                URL url = URI.create(hardcoverImage.getUrl()).toURL();
                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.error("Failed to download image from URL: {}", hardcoverImage.getUrl(), e);
                return null;
            }

            var image = Image.builder()
                    .color(hardcoverImage.getColor())
                    .extension(extension)
                    .filename(filename)
                    .directory(directory)
                    .width(hardcoverImage.getWidth())
                    .height(hardcoverImage.getHeight())
                    .imageType(imageType)
                    .hardcoverId(hardcoverImage.getId())
                    .color(hardcoverImage.getColor())
                    .build();

            result = imageService.saveImage(image);
        }
        return result;
    }

    public String audiobookDirectory(Book book) {
        var dataDir = mediaSanctumConfig.dataDir();
        var audiobookDirectory = Path.of(dataDir, "audiobooks",
                book.getAuthor().getName(), book.getTitle()).toString();
        return createDirectory(audiobookDirectory);
    }


    private String bookDirectory(Book book) {
        var author = book.getAuthor();
        var authorDir = authorDirectory(author);
        var bookDirectory = Path.of(authorDir, book.getTitle()).toString();
        return createDirectory(bookDirectory);
    }


    private String authorDirectory(Author author) {
        var dataDir = mediaSanctumConfig.dataDir();
        var authorDirectory = Path.of(dataDir, "books", author.getName()).toString();
        return createDirectory(authorDirectory);
    }

    private String createDirectory(String directory) {
        try {
            Files.createDirectories(Path.of(directory));
            return directory;
        } catch (IOException e) {
            log.error("Failed to create directory: {}", directory, e);
            throw new UncheckedIOException(e);
        }
    }
}
