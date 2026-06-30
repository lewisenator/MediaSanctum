package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.config.RequestContext;
import com.media_sanctum.backend.resource.AudiobookProgressResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.EbookProgressResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import com.media_sanctum.backend.resource.UpsertAudiobookProgressRequest;
import com.media_sanctum.backend.resource.UpsertEbookProgressRequest;
import com.media_sanctum.backend.service.AudiobookProgressService;
import com.media_sanctum.backend.service.BookService;
import com.media_sanctum.backend.service.EbookProgressService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final BookService bookService;
    private final EbookProgressService ebookProgressService;
    private final AudiobookProgressService audiobookProgressService;
    private final RequestContext requestContext;

    public ProgressController(
            BookService bookService,
            EbookProgressService ebookProgressService,
            AudiobookProgressService audiobookProgressService,
            RequestContext requestContext
    ) {
        this.bookService = bookService;
        this.ebookProgressService = ebookProgressService;
        this.audiobookProgressService = audiobookProgressService;
        this.requestContext = requestContext;
    }

    @PostMapping("/{bookId}/ebook")
    public ResponseEntity<DataResponse<EbookProgressResponse>> upsertEbookProgress(
            @PathVariable @NotBlank String bookId,
            @NotNull @RequestBody UpsertEbookProgressRequest progressRequest
    ) {
        var book = bookService.getBook(bookId);
        if (book == null) {
            return bookNotFoundError(bookId);
        }

        var progressResponse = ebookProgressService.upsert(book, requestContext.getUser(), progressRequest);
        return ResponseEntity.ok(DataResponse.data(progressResponse));
    }

    @PostMapping("/{bookId}/audiobook")
    public ResponseEntity<DataResponse<AudiobookProgressResponse>> upsertAudiobookProgress(
            @PathVariable @NotBlank String bookId,
            @NotNull @RequestBody UpsertAudiobookProgressRequest progressRequest
    ) {
        var book = bookService.getBook(bookId);
        if (book == null) {
            return bookNotFoundError(bookId);
        }

        var progressResponse = audiobookProgressService.upsert(book, requestContext.getUser(), progressRequest);
        return ResponseEntity.ok(DataResponse.data(progressResponse));
    }

    private <T> ResponseEntity<DataResponse<T>> bookNotFoundError(String bookId) {
        var error = ErrorResponse.builder()
                .error("BOOK_NOT_FOUND")
                .message("Book with id %s not found".formatted(bookId))
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DataResponse.error(error));
    }
}
