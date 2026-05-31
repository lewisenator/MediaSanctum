package com.media_sanctum.backend.manager;

import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.client.hardcover.model.HardcoverImage;
import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.entity.Author;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.entity.ImageType;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.service.AuthorService;
import com.media_sanctum.backend.service.BookService;
import com.media_sanctum.backend.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CatalogueManager {

    private static final Pattern SAFE_IMAGE_PATTERN = Pattern.compile("^.*(jpg|jpeg|png|gif|webp)$");

    private final BookService bookService;
    private final AuthorService authorService;
    private final ImageService imageService;
    private final HardcoverClient hardcoverClient;
    private final MediaSanctumConfig mediaSanctumConfig;

    public CatalogueManager(
            BookService bookService,
            AuthorService authorService,
            ImageService imageService,
            HardcoverClient hardcoverClient,
            MediaSanctumConfig mediaSanctumConfig
    ) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.imageService = imageService;
        this.hardcoverClient = hardcoverClient;
        this.mediaSanctumConfig = mediaSanctumConfig;
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
                .build();

        var savedBook = bookService.saveBook(book);

        return BookService.toResponse(savedBook);
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

        author.setImage(upsertMugshot(author, hardcoverAuthor.getCachedImage()));

        var savedAuthor = authorService.saveAuthor(author);

        return savedAuthor;
    }

    private Image upsertMugshot(Author author, HardcoverImage hardcoverImage) {
        var result = imageService.findByHardcoverId(hardcoverImage.getId()).orElse(null);
        if (result == null) {
            var authorDirectory = createAuthorDirectory(author);
            var matcher = SAFE_IMAGE_PATTERN.matcher(hardcoverImage.getUrl());
            if (!matcher.matches()) {
                log.error("Invalid image URL: {}", hardcoverImage.getUrl());
                return null;
            }
            var extension = matcher.group(1);
            var filename = "mugshot." + extension;
            var targetPath = Path.of(authorDirectory, filename);

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
                    .directory(authorDirectory)
                    .width(hardcoverImage.getWidth())
                    .height(hardcoverImage.getHeight())
                    .imageType(ImageType.MUGSHOT)
                    .hardcoverId(hardcoverImage.getId())
                    .color(hardcoverImage.getColor())
                    .build();

            result = imageService.saveImage(image);
        }
        return result;
    }

    private String createAuthorDirectory(Author author) {
        var dataDir = mediaSanctumConfig.dataDir();
        var authorPath = Path.of(dataDir, author.getName()).toString();
        try {
            Files.createDirectories(Path.of(authorPath));
            return authorPath;
        } catch (IOException e) {
            log.error("Failed to create author directory: {}", authorPath, e);
            throw new UncheckedIOException(e);
        }
    }
}
