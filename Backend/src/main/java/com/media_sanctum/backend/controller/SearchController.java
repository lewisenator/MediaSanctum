package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.resource.AuthorResponse;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.SearchResponse;
import com.media_sanctum.backend.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/authors")
    public ResponseEntity<DataResponse<SearchResponse<AuthorResponse>>> searchAuthors(
            @NotBlank @Param("q") String q
    ) {
        var searchResponse = searchService.searchAuthors(q);
        return ResponseEntity.ok(DataResponse.data(searchResponse));
    }

    @PostMapping("/books")
    public ResponseEntity<DataResponse<SearchResponse<BookResponse>>> searchBooks(
            @NotBlank @Param("q") String q
    ) {
        var searchResponse = searchService.searchBooks(q);
        return ResponseEntity.ok(DataResponse.data(searchResponse));
    }
}
