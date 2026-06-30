package com.media_sanctum.backend.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.entity.BookFile;
import com.media_sanctum.backend.entity.EditionType;
import com.media_sanctum.backend.entity.audio.FFProbe;
import com.media_sanctum.backend.service.BookFileService;
import com.media_sanctum.backend.service.BookService;
import com.media_sanctum.backend.service.MediaStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class BookFileManager {

    private static final Pattern SAFE_EBOOK_FILE_PATTERN = Pattern.compile("^.*(pdf|epub|mobi)$");
    private static final Pattern SAFE_AUDIOBOOK_FILE_PATTERN = Pattern.compile("^.*(m4b)$");

    private final BookService bookService;
    private final BookFileService bookFileService;
    private final MediaStorageService mediaStorageService;
    private final ObjectMapper objectMapper;

    public BookFileManager(
            BookService bookService,
            BookFileService bookFileService,
            MediaStorageService mediaStorageService,
            ObjectMapper objectMapper
    ) {
        this.bookService = bookService;
        this.bookFileService = bookFileService;
        this.mediaStorageService = mediaStorageService;
        this.objectMapper = objectMapper;
    }

    public void uploadBookFile(String bookId, EditionType editionType, MultipartFile file) {
        var book = bookService.getBook(bookId);
        var ctx = buildUploadContext(book, editionType, file);

        writeFileToDisk(file, ctx);

        var bookFile = buildBookFile(book, editionType, file, ctx);
        bookFile = bookFileService.saveBookFile(bookFile);

        switch (editionType) {
            case EBOOK -> book.setEbookFile(bookFile);
            case AUDIOBOOK -> book.setAudiobookFile(bookFile);
            default -> throw new IllegalStateException("Unexpected value: " + editionType);
        }
        bookService.saveBook(book);
    }

    private FileUploadContext buildUploadContext(Book book, EditionType editionType, MultipartFile file) {
        var uploadDirectory = switch (editionType) {
            case EBOOK -> mediaStorageService.bookDirectory(book);
            case AUDIOBOOK -> mediaStorageService.audiobookDirectory(book);
        };
        var fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        var matcher = switch (editionType) {
            case EBOOK -> SAFE_EBOOK_FILE_PATTERN.matcher(fileName);
            case AUDIOBOOK -> SAFE_AUDIOBOOK_FILE_PATTERN.matcher(fileName);
        };
        if (!matcher.matches()) {
            var message = String.format("Invalid %s file extension: %s", editionType, fileName);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        var extension = matcher.group(1);
        var desiredFilename = book.getTitle() + "." + extension;
        var filePath = Path.of(uploadDirectory, desiredFilename);
        return new FileUploadContext(extension, desiredFilename, uploadDirectory, filePath);
    }

    private void writeFileToDisk(MultipartFile file, FileUploadContext ctx) {
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, ctx.filePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new UncheckedIOException(e);
        }
    }

    private BookFile buildBookFile(Book book, EditionType editionType, MultipartFile file, FileUploadContext ctx) {
        var id = UUID.randomUUID().toString();
        var url = "/public/%s-files/%s.%s".formatted(editionType.getPathValue(), id, ctx.extension());
        var bookFile = BookFile.builder()
                .id(id)
                .url(url)
                .size(file.getSize())
                .hash(BookFileService.getFileMD5(ctx.filePath().toString()))
                .directory(ctx.uploadDirectory())
                .filename(ctx.desiredFilename())
                .contentType(file.getContentType())
                .extension(ctx.extension())
                .editionType(editionType)
                .book(book)
                .build();
        if (editionType == EditionType.AUDIOBOOK) {
            bookFile.setFfprobe(ffProbe(ctx.filePath().toString()));
        }
        return bookFile;
    }

    private record FileUploadContext(
            String extension,
            String desiredFilename,
            String uploadDirectory,
            Path filePath
    ) {}

    private FFProbe ffProbe(String filePath) {
        try {
            var process = new ProcessBuilder(
                    "ffprobe", "-v", "quiet", "-print_format", "json",
                    "-show_chapters", "-show_format", "-show_streams", filePath
            ).start();
            var output = process.getInputStream().readAllBytes();
            process.waitFor();
            return objectMapper.readValue(output, FFProbe.class);
        } catch (IOException | InterruptedException e) {
            log.error("ffprobe failed for: {}", filePath, e);
            return null;
        }
    }
}
