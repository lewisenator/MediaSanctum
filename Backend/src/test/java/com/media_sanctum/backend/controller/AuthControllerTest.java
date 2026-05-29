package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.model.UserModel;
import com.media_sanctum.backend.resource.LoginRequest;
import com.media_sanctum.backend.service.UserService;
import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends BaseControllerTest {

    @Autowired
    protected UserService userService;

    protected String password = UUID.randomUUID().toString();
    protected String email = UUID.randomUUID() + "@example.com";
    protected UserModel userModel;
    @BeforeEach
    public void setup() {
        super.setup();
        var createUser = CreateUserModel.builder()
                .email(email)
                .password(password)
                .firstName("Test")
                .lastName("User")
                .build();
        userModel = userService.createUser(createUser);
    }

    @Test
    public void login_ok() {
        var loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        var response = restClient.post()
                .uri("/api/auth/login")
                .body(loginRequest)
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().headerNames()).contains("set-cookie");

        var expectedRespnse = String.format("""
            {
                "data": {
                    "user": {
                        "email": "%s",
                        "lastName": "User",
                        "firstName": "Test",
                        "authorities": []
                    },
                    "accessToken": "{{ANY-STRING}}"
                },
                "error": null
            }
        """.formatted(email));

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedRespnse);
    }

    @Test
    public void login_badPassword() {
        var loginRequest = LoginRequest.builder()
                .email(email)
                .password(UUID.randomUUID().toString())
                .build();

        var response = restClient.post()
                .uri("/api/auth/login")
                .body(loginRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().headerNames()).doesNotContain("set-cookie");

        var expectedRespnse = """
            {
                "data": null,
                "error": {
                "timestamp" : "{{TIMESTAMP:10:SECONDS}}",
                    "error" : "BadCredentialsException",
                    "message" : "Bad credentials"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedRespnse);
    }

    @Test
    public void login_badEmail() {
        var loginRequest = LoginRequest.builder()
                .email(UUID.randomUUID() + "@example.com")
                .password(password)
                .build();

        var response = restClient.post()
                .uri("/api/auth/login")
                .body(loginRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().headerNames()).doesNotContain("set-cookie");

        var expectedRespnse = """
            {
                "data": null,
                "error": {
                    "timestamp" : "{{TIMESTAMP:10:SECONDS}}",
                    "error" : "BadCredentialsException",
                    "message" : "Bad credentials"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedRespnse);
    }

    @Test
    public void refresh_ok() throws Exception {
        var loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        var loginResponse = restClient.post()
                .uri("/api/auth/login")
                .body(loginRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        var setCookieHheaders = loginResponse.getHeaders().get("set-cookie");

        assertThat(setCookieHheaders).hasSize(1);

        var refreshTokenCookie = setCookieHheaders.get(0);
        var refreshTokenCookieName = refreshTokenCookie.split("=")[0];
        var refreshTokenCookieValue = refreshTokenCookie.split("=")[1];

        // Perform actual refresh

        var response = restClient.post()
                .uri("/api/auth/refresh")
                .cookie(refreshTokenCookieName, refreshTokenCookieValue)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().headerNames()).contains("set-cookie");

        var expectedRespnse = String.format("""
            {
                "data": {
                    "user": {
                        "email": "%s",
                        "lastName": "User",
                        "firstName": "Test",
                        "authorities": []
                    },
                    "accessToken": "{{ANY-STRING}}"
                },
                "error": null
            }
        """.formatted(email));

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedRespnse);
    }

    @Test
    public void refresh_badCookie() throws Exception {
        var response = restClient.post()
                .uri("/api/auth/refresh")
                .cookie("refresh-token", "bad")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().headerNames()).doesNotContain("set-cookie");

        var expectedResponse = """
            {
                "data": null,
                "error": {
                    "timestamp" : "{{TIMESTAMP:10:SECONDS}}",
                    "error" : "BadCredentialsException",
                    "message" : "{{ANY-STRING}}"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedResponse);
    }
}
