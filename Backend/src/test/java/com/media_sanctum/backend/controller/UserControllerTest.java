package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseControllerTest {

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    public void getUser_ok() throws Exception {
        var authResponse = login();
        assertThat(authResponse).isNotNull();
        var accessToken = authResponse.getAccessToken();

        var response = restClient.get()
                .uri("/api/users")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}