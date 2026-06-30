package com.media_sanctum.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.resource.AddBookRequest;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.UpsertAudiobookProgressRequest;
import com.media_sanctum.backend.resource.UpsertEbookProgressRequest;
import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgressControllerTest extends BaseControllerTest {

    private static final Integer JURASSIC_PARK_BOOK_ID = 9724;

    private static final String EBOOK_PROGRESS_CONTRACT = """
            {
                "id": "{{UUID}}",
                "epubcfi": "{{STRING}}",
                "percent": "{{INTEGER}}",
                "currentChapter": "{{INTEGER?nullable=true}}",
                "totalChapters": "{{INTEGER?nullable=true}}",
                "currentPage": "{{INTEGER?nullable=true}}",
                "totalPages": "{{INTEGER?nullable=true}}",
                "createdAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}",
                "updatedAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
            }
            """;

    private static final String AUDIOBOOK_PROGRESS_CONTRACT = """
            {
                "id": "{{UUID}}",
                "percent": "{{INTEGER}}",
                "currentChapter": "{{INTEGER?nullable=true}}",
                "totalChapters": "{{INTEGER?nullable=true}}",
                "seconds": "{{INTEGER}}",
                "duration": "{{INTEGER}}",
                "createdAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}",
                "updatedAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
            }
            """;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void upsertEbookProgress_bookNotFound_returns404() {
        var request = UpsertEbookProgressRequest.builder()
                .epubcfi("/6/4[chap01]!/4/2/1:0")
                .percent(25)
                .build();

        var response = restClient.post()
                .uri("/api/progress/%s/ebook".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .body(request)
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
    public void upsertEbookProgress_ok() throws Exception {
        var bookId = addBook();

        var request = UpsertEbookProgressRequest.builder()
                .epubcfi("/6/4[chap01]!/4/2/1:0")
                .percent(25)
                .currentPage(50)
                .totalPages(350)
                .currentChapter(3)
                .totalChapters(20)
                .build();

        var response = restClient.post()
                .uri("/api/progress/%s/ebook".formatted(bookId))
                .header("Authorization", "Bearer " + getAccessToken())
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(EBOOK_PROGRESS_CONTRACT));

        // Upsert with updated values — verify the same record is updated
        var updatedRequest = UpsertEbookProgressRequest.builder()
                .epubcfi("/6/8[chap03]!/4/2/1:0")
                .percent(60)
                .currentPage(120)
                .totalPages(350)
                .currentChapter(7)
                .totalChapters(20)
                .build();

        response = restClient.post()
                .uri("/api/progress/%s/ebook".formatted(bookId))
                .header("Authorization", "Bearer " + getAccessToken())
                .body(updatedRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(EBOOK_PROGRESS_CONTRACT));
        assertThat(response.getBody()).contains("/6/8[chap03]!/4/2/1:0");
    }

    @Test
    public void upsertAudiobookProgress_bookNotFound_returns404() {
        var request = UpsertAudiobookProgressRequest.builder()
                .percent(25)
                .seconds(1800)
                .duration(7200)
                .build();

        var response = restClient.post()
                .uri("/api/progress/%s/audiobook".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .body(request)
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
    public void upsertAudiobookProgress_ok() throws Exception {
        var bookId = addBook();

        var request = UpsertAudiobookProgressRequest.builder()
                .percent(25)
                .seconds(1800)
                .duration(7200)
                .currentChapter(3)
                .totalChapters(20)
                .build();

        var response = restClient.post()
                .uri("/api/progress/%s/audiobook".formatted(bookId))
                .header("Authorization", "Bearer " + getAccessToken())
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(AUDIOBOOK_PROGRESS_CONTRACT));

        // Upsert with updated values — verify the same record is updated
        var updatedRequest = UpsertAudiobookProgressRequest.builder()
                .percent(50)
                .seconds(3600)
                .duration(7200)
                .currentChapter(7)
                .totalChapters(20)
                .build();

        response = restClient.post()
                .uri("/api/progress/%s/audiobook".formatted(bookId))
                .header("Authorization", "Bearer " + getAccessToken())
                .body(updatedRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(AUDIOBOOK_PROGRESS_CONTRACT));
        assertThat(response.getBody()).contains("3600");
    }

    private String addBook() throws Exception {
        var response = restClient.post()
                .uri("/api/books")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(AddBookRequest.builder().hardcoverId(JURASSIC_PARK_BOOK_ID).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        return objectMapper.readValue(response.getBody(),
                new TypeReference<DataResponse<BookResponse>>() {}).getData().getId();
    }
}
