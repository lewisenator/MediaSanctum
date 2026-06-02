package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.entity.EditionType;
import com.media_sanctum.backend.manager.CatalogueManager;
import com.media_sanctum.backend.resource.AddBookRequest;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import com.media_sanctum.backend.service.BookService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    private final CatalogueManager catalogueManager;
    private final BookService bookService;

    public BooksController(CatalogueManager catalogueManager, BookService bookService) {
        this.catalogueManager = catalogueManager;
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<DataResponse<BookResponse>> addBook(
            @NotNull @RequestBody AddBookRequest addBookRequest
    ) {
        var hardcoverId = addBookRequest.getHardcoverId();
        var book = catalogueManager.addBook(hardcoverId);
        return ResponseEntity.ok(DataResponse.data(book));
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<BookResponse>>> getBooks() {
        var books = bookService.getBooksResponse();
        return ResponseEntity.ok(DataResponse.data(books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<BookResponse>> getBook(
            @PathVariable String id
    ) {
        var book = bookService.getBookResponse(id);
        if (book == null) {
            var error = ErrorResponse.builder()
                    .error("BOOK_NOT_FOUND")
                    .message("Book with id %s not found".formatted(id))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<BookResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse);
        }
        return ResponseEntity.ok(DataResponse.data(book));
    }

    @PostMapping(
            value = "/{id}/{editionValue}/upload",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public ResponseEntity<DataResponse<BookResponse>> updateBook(
            @PathVariable String id,
            @PathVariable String editionValue,
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            var error = ErrorResponse.builder()
                    .error("FILE_MISSING")
                    .message("You must pass a file value in the file form parameter")
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<BookResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dataResponse);
        }

        var editionType = EditionType.fromPathValue(editionValue);
        if (editionType == null) {
            var error = ErrorResponse.builder()
                    .error("INVALID_EDITION_TYPE")
                    .message("Invalid edition type (must be audiobook or ebook): %s".formatted(editionValue))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<BookResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dataResponse);
        }

        if (!bookService.exists(id)) {
            var error = ErrorResponse.builder()
                    .error("BOOK_NOT_FOUND")
                    .message("Book with id %s not found".formatted(id))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<BookResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse);
        }

        var book = catalogueManager.uploadBookFile(id, editionType, file);

        return ResponseEntity.ok(DataResponse.data(book));
    }
}
