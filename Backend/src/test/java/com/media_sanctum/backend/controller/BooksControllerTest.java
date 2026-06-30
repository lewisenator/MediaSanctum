package com.media_sanctum.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.resource.AddBookRequest;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BooksControllerTest extends BaseControllerTest {

    private static final Integer JURASSIC_PARK_BOOK_ID = 9724;

    public static final String TAG_CONTRACT = """
            {
                "tag": "{{STRING}}",
                "count": "{{INTEGER}}",
                "category": "{{STRING}}"
            }
            """;

    public static final String BOOK_CONTRACT = """
            {
                "id": "{{UUID}}",
                "headline": "{{STRING}}",
                "title": "{{STRING}}",
                "slug": "{{STRING}}",
                "subtitle": "{{STRING}}",
                "description": "{{STRING}}",
                "releaseYear": "{{INTEGER}}",
                "pages": "{{INTEGER}}",
                "audioSeconds": "{{INTEGER?nullable=true}}",
                "createdAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}",
                "updatedAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}",
                "rating": "{{FLOAT}}",
                "ratingsCount": "{{INTEGER}}",
                "tags": "{{STRING-ARRAY}}",
                "author": "{{OBJECT}}",
                "audiobookEdition": "{{OBJECT}}",
                "ebookEdition": "{{OBJECT}}",
                "featuredSeries": "{{OBJECT}}",
                "tags": "{{OBJECT-ARRAY?contract=%s}}",
                "ebookFile": "{{OBJECT?nullable=true}}",
                "audiobookFile": "{{OBJECT?nullable=true}}",
                "ebookProgress": "{{OBJECT?nullable=true}}",
                "audiobookProgress": "{{OBJECT?nullable=true}}"
            }
            """.formatted(URLEncoder.encode(TAG_CONTRACT, StandardCharsets.UTF_8));

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addBook_getBook_and_getBooks_ok() throws Exception {
        // Add book
        var addBookRequest = AddBookRequest.builder()
                .hardcoverId(JURASSIC_PARK_BOOK_ID)
                .build();

        var response = restClient.post()
                .uri("/api/books")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(addBookRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(BOOK_CONTRACT));

        var bookResponseType = new TypeReference<DataResponse<BookResponse>>() {};
        var bookResponse = objectMapper.readValue(response.getBody(), bookResponseType);
        var id = bookResponse.getData().getId();

        // Get that book by id
        response = restClient.get()
                .uri("/api/books/" + id)
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(BOOK_CONTRACT));

        // Get all books
        response = restClient.get()
                .uri("/api/books")
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).contains(id);

        var booksContract = """
                {
                    "data": "{{OBJECT-ARRAY?allowEmpty=false&contract=%s}}",
                    "error": null
                }
                """.formatted(URLEncoder.encode(BOOK_CONTRACT, StandardCharsets.UTF_8));

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(booksContract);
    }

    @Test
    public void updateBook_fileMissing_returns400() {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", new ByteArrayResource(new byte[0]) {
            @Override public String getFilename() { return "book.pdf"; }
        });

        var response = restClient.post()
                .uri("/api/books/%s/ebook/upload".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract("""
                        {
                            "data": null,
                            "error": {
                                "error": "FILE_MISSING",
                                "message": "{{STRING}}",
                                "timestamp": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
                            }
                        }
                        """);
    }

    @Test
    public void updateBook_invalidEditionType_returns400() {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", new ByteArrayResource("content".getBytes()) {
            @Override public String getFilename() { return "book.pdf"; }
        });

        var response = restClient.post()
                .uri("/api/books/%s/invalid/upload".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract("""
                        {
                            "data": null,
                            "error": {
                                "error": "INVALID_EDITION_TYPE",
                                "message": "{{STRING}}",
                                "timestamp": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
                            }
                        }
                        """);
    }

    @Test
    public void updateBook_bookNotFound_returns404() {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", new ByteArrayResource("content".getBytes()) {
            @Override public String getFilename() { return "book.pdf"; }
        });

        var response = restClient.post()
                .uri("/api/books/%s/ebook/upload".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract("""
                        {
                            "data": null,
                            "error": {
                                "error": "BOOK_NOT_FOUND",
                                "message": "{{STRING}}",
                                "timestamp": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
                            }
                        }
                        """);
    }

    @Test
    public void updateBook_ebook_ok() throws Exception {
        var addBookRequest = AddBookRequest.builder()
                .hardcoverId(JURASSIC_PARK_BOOK_ID)
                .build();

        var addResponse = restClient.post()
                .uri("/api/books")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(addBookRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        var bookId = objectMapper.readValue(addResponse.getBody(),
                new TypeReference<DataResponse<BookResponse>>() {}).getData().getId();

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", new ByteArrayResource("%PDF-1.4 test content".getBytes()) {
            @Override public String getFilename() { return "jurassic-park.pdf"; }
        });

        var response = restClient.post()
                .uri("/api/books/%s/ebook/upload".formatted(bookId))
                .header("Authorization", "Bearer " + getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(BOOK_CONTRACT));
    }

    @Test
    public void getBook_notFound() {
        var response = restClient.get()
                .uri("/api/books/%s".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        var contract = """
                {
                    "data": null,
                    "error": {
                        "error": "BOOK_NOT_FOUND",
                        "message": "{{STRING}}",
                        "timestamp": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
                    }
                }
                """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(contract);
    }
}