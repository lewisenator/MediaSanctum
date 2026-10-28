package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.resource.CreateUserRequest;
import com.media_sanctum.backend.resource.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseControllerTest {

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    public void getUser_ok() throws Exception {
        var response = restClient.get()
                .uri("/api/users")
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private String email() {
        return UUID.randomUUID() + "@example.com";
    }

    private org.springframework.http.ResponseEntity<String> post(Object body) {
        return restClient.post()
                .uri("/api/users")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);
    }

    private org.springframework.http.ResponseEntity<String> put(String id, Object body) {
        return restClient.put()
                .uri("/api/users/" + id)
                .header("Authorization", "Bearer " + getAccessToken())
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, doNothingErrorHandler)
                .toEntity(String.class);
    }

    @Test
    public void createUser_ok_noPasswordInResponse() {
        var email = email();
        var response = post(CreateUserRequest.builder()
                .email(email).password("correct-horse").firstName("Ada").lastName("Lovelace").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains(email).contains("Ada");
        assertThat(response.getBody()).doesNotContainIgnoringCase("password").doesNotContain("correct-horse");
        assertThat(userService.getUserByEmail(email).getPasswordHash()).isNotEqualTo("correct-horse");
    }

    @Test
    public void createUser_invalid() {
        var response = post(CreateUserRequest.builder().email("not-an-email").password("short").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("VALIDATION_ERROR").contains("email").contains("password");
    }

    @Test
    public void createUser_duplicateEmail() {
        var email = email();
        var request = CreateUserRequest.builder().email(email).password("correct-horse").build();
        assertThat(post(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var response = post(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("EMAIL_ALREADY_EXISTS");
    }

    @Test
    public void updateUser_ok_partialUpdate() {
        var email = email();
        post(CreateUserRequest.builder().email(email).password("correct-horse").firstName("Ada").build());
        var id = userService.getUserByEmail(email).getId();

        var response = put(id, UpdateUserRequest.builder().lastName("Byron").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Byron").contains("Ada").contains(email);
        assertThat(response.getBody()).doesNotContainIgnoringCase("password");
    }

    @Test
    public void updateUser_notFound() {
        var response = put(UUID.randomUUID().toString(), UpdateUserRequest.builder().firstName("X").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("USER_NOT_FOUND");
    }

    @Test
    public void updateUser_invalid() {
        var email = email();
        post(CreateUserRequest.builder().email(email).password("correct-horse").build());
        var id = userService.getUserByEmail(email).getId();

        var response = put(id, UpdateUserRequest.builder().email("nope").password("short").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("VALIDATION_ERROR");
    }
}
