package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.manager.CatalogueManager;
import com.media_sanctum.backend.resource.AddAuthorRequest;
import com.media_sanctum.backend.resource.AuthorResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import com.media_sanctum.backend.service.AuthorService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorsController {

    private final CatalogueManager catalogueManager;
    private final AuthorService authorService;

    public AuthorsController(CatalogueManager catalogueManager, AuthorService authorService) {
        this.catalogueManager = catalogueManager;
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<DataResponse<AuthorResponse>> addAuthor(
            @NotNull @RequestBody AddAuthorRequest addAuthorRequest
    ) {
        var hardcoverId = addAuthorRequest.getHardcoverId();
        var author = catalogueManager.addAuthor(hardcoverId);
        var authorResponse = AuthorService.toResponse(author);
        return ResponseEntity.ok(DataResponse.data(authorResponse));
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<AuthorResponse>>> getAuthors() {
        var authors = authorService.getAuthorsResponse();
        return ResponseEntity.ok(DataResponse.data(authors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<AuthorResponse>> getAuthor(
            @PathVariable String id
    ) {
        var author = authorService.getAuthorResponse(id);
        if (author == null) {
            var error = ErrorResponse.builder()
                    .error("AUTHOR_NOT_FOUND")
                    .message("Author with id %s not found".formatted(id))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<AuthorResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse);
        }
        return ResponseEntity.ok(DataResponse.data(author));
    }
}
