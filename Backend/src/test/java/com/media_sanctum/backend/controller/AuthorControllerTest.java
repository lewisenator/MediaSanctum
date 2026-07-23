package com.media_sanctum.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.resource.AddAuthorRequest;
import com.media_sanctum.backend.resource.AuthorResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorControllerTest extends BaseControllerTest {

    private static final Integer AUTHOR_HARDCOVER_ID = 77582;

    public static final String LINK_CONTRACT = """
            {
                "title": "{{STRING}}",
                "url": "{{STRING}}"
            }
            """;

    public static final String IMAGE_CONTRACT = """
            {
                "id": "{{UUID}}",
                "url": "{{STRING}}",
                "color": "{{STRING}}",
                "width": "{{INTEGER}}",
                "height": "{{INTEGER}}"
            }
            """;

    public static final String AUTHOR_CONTRACT = """
            {
                "id": "{{UUID}}",
                "name": "{{STRING}}",
                "title": "{{STRING?nullable=true}}",
                "alternateNames": "{{STRING-ARRAY}}",
                "slug": "{{STRING}}",
                "bio": "{{STRING}}",
                "bornYear": "{{INTEGER?nullable=true}}",
                "deathYear": "{{INTEGER?nullable=true}}",
                "booksCount": "{{INTEGER}}",
                "libraryBooksCount": "{{INTEGER?nullable=true}}",
                "links": "{{OBJECT-ARRAY?contract=%s}}",
                "createdAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}",
                "updatedAt": "{{TIMESTAMP?amount=1&unit=MINUTES}}",
                "image": %s
            }
            """.formatted(
                    URLEncoder.encode(LINK_CONTRACT, StandardCharsets.UTF_8),
                    IMAGE_CONTRACT

            );

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addAuthor_getAuthor_getAuthors_ok() throws Exception  {
        // Add author
        var addAuthorRequest = AddAuthorRequest.builder()
                .hardcoverId(AUTHOR_HARDCOVER_ID)
                .build();

        var response = restClient.post()
                .uri("/api/authors")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(addAuthorRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(AUTHOR_CONTRACT));

        var authorResponseType = new TypeReference<DataResponse<AuthorResponse>>() {};
        var authorResponse = objectMapper.readValue(response.getBody(), authorResponseType);
        var id = authorResponse.getData().getId();

        // Get Author by id
        response = restClient.get()
                .uri("/api/authors/" + id)
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(DATA_CONTRACT.formatted(AUTHOR_CONTRACT));

        // Get all authors
        response = restClient.get()
                .uri("/api/authors")
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).contains(id);

        var authorsContract = """
                {
                    "data": "{{OBJECT-ARRAY?allowEmpty=false&contract=%s}}",
                    "error": null
                }
                """.formatted(URLEncoder.encode(AUTHOR_CONTRACT, StandardCharsets.UTF_8));

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(authorsContract);
    }

    @Test
    public void getAuthor_notFound() {
        var response = restClient.get()
                .uri("/api/authors/%s".formatted(UUID.randomUUID()))
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        var contract = """
                {
                    "data": null,
                    "error": {
                        "error": "AUTHOR_NOT_FOUND",
                        "message": "{{STRING}}",
                        "timestamp": "{{TIMESTAMP?amount=1&unit=MINUTES}}"
                    }
                }
                """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(contract);
    }
}
