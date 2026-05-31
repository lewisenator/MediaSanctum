package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.manager.CatalogueManager;
import com.media_sanctum.backend.resource.AddBookRequest;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.service.BookService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(DataResponse.data(book));
    }
}
