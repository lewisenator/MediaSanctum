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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicControllerTest extends BaseControllerTest {

    private static final Integer JURASSIC_PARK_BOOK_ID = 9724;

    private static final String ERROR_CONTRACT = """
            {
                "data": null,
                "error": {
                    "error": "{{STRING}}",
                    "message": "{{STRING}}",
                    "timestamp": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
                }
            }
            """;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getImage_invalidFilename_returns400() {
        var response = restClient.get()
                .uri("/public/images/foo.bmp")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonAssertionBuilder.assertThatJson(response.getBody()).matchesContract(ERROR_CONTRACT);
    }

    @Test
    public void getEbookFile_invalidFilename_returns400() {
        var response = restClient.get()
                .uri("/public/ebook-files/foo.pdf")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonAssertionBuilder.assertThatJson(response.getBody()).matchesContract(ERROR_CONTRACT);
    }

    @Test
    public void getAudiobookFile_invalidFilename_returns400() {
        var response = restClient.get()
                .uri("/public/audiobook-files/foo.mp3")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonAssertionBuilder.assertThatJson(response.getBody()).matchesContract(ERROR_CONTRACT);
    }

    @Test
    public void getImage_ok() throws Exception {
        var addResponse = restClient.post()
                .uri("/api/books")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(AddBookRequest.builder().hardcoverId(JURASSIC_PARK_BOOK_ID).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(String.class);

        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var book = objectMapper.readValue(addResponse.getBody(),
                new TypeReference<DataResponse<BookResponse>>() {}).getData();
        var imageUrl = book.getAuthor().getImage().getUrl();

        var response = restClient.get()
                .uri(imageUrl)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {})
                .toEntity(byte[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().getType()).isEqualTo("image");
        assertThat(response.getBody()).isNotEmpty();
    }
}
