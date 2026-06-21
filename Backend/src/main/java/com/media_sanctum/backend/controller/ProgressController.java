package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.manager.CatalogueManager;
import com.media_sanctum.backend.resource.AudiobookProgressResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import com.media_sanctum.backend.resource.EbookProgressResponse;
import com.media_sanctum.backend.resource.UpsertAudiobookProgressRequest;
import com.media_sanctum.backend.resource.UpsertEbookProgressRequest;
import com.media_sanctum.backend.service.BookService;
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

    private final CatalogueManager catalogueManager;
    private final BookService bookService;

    public ProgressController(
            CatalogueManager catalogueManager,
            BookService bookService
    ) {
        this.catalogueManager = catalogueManager;
        this.bookService = bookService;
    }

    @PostMapping("/{bookId}/ebook")
    public ResponseEntity<DataResponse<EbookProgressResponse>> upsertProgress(
            @PathVariable @NotBlank String bookId,
            @NotNull @RequestBody UpsertEbookProgressRequest progressRequest
    ) {
        var book = bookService.getBook(bookId);
        if (book == null) {
            var error = ErrorResponse.builder()
                    .error("BOOK_NOT_FOUND")
                    .message("Book with id %s not found".formatted(bookId))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<EbookProgressResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse);
        }

        var progressResponse = catalogueManager.upsertEbookProgress(book, progressRequest);

        return ResponseEntity.ok(DataResponse.data(progressResponse));
    }

    @PostMapping("/{bookId}/audiobook")
    public ResponseEntity<DataResponse<AudiobookProgressResponse>> upsertProgress(
            @PathVariable @NotBlank String bookId,
            @NotNull @RequestBody UpsertAudiobookProgressRequest progressRequest
    ) {
        var book = bookService.getBook(bookId);
        if (book == null) {
            var error = ErrorResponse.builder()
                    .error("BOOK_NOT_FOUND")
                    .message("Book with id %s not found".formatted(bookId))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<AudiobookProgressResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse);
        }

        var progressResponse = catalogueManager.upsertAudiobookProgress(book, progressRequest);

        return ResponseEntity.ok(DataResponse.data(progressResponse));
    }
}
