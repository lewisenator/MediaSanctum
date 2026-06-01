package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.BaseControllerTest;
import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.model.UserModel;
import com.media_sanctum.backend.resource.LoginRequest;
import com.media_sanctum.backend.service.UserService;
import com.media_sanctum.backend.utils.json.JsonAssertionBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
        loginExpectingSuccess();
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

        var expectedResponse = """
            {
                "data": null,
                "error": {
                "timestamp" : "{{TIMESTAMP?amount=10&unit=SECONDS}}",
                    "error" : "BadCredentialsException",
                    "message" : "Bad credentials"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedResponse);
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

        var expectedResponse = """
            {
                "data": null,
                "error": {
                    "timestamp" : "{{TIMESTAMP?amount=10&unit=SECONDS}}",
                    "error" : "BadCredentialsException",
                    "message" : "Bad credentials"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedResponse);
    }

    @Test
    public void refresh_ok() throws Exception {
        var loginResponse = loginExpectingSuccess();
        var cookie = getRefreshTokenCookie(loginResponse);

        // Perform actual refresh
        var response = restClient.post()
                .uri("/api/auth/refresh")
                .cookie(cookie.getKey(), cookie.getValue())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().headerNames()).contains("set-cookie");

        var expectedResponse = String.format("""
            {
                "data": {
                    "user": {
                        "email": "%s",
                        "lastName": "User",
                        "firstName": "Test",
                        "authorities": []
                    },
                    "accessToken": "{{STRING}}"
                },
                "error": null
            }
        """.formatted(email));

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedResponse);
    }

    @Test
    public void refresh_badCookie() {
        refreshExpectingBadTokenError("bad-cookie");
    }

    @Test
    public void logout_ok() throws Exception {
        // login
        var loginResponse = loginExpectingSuccess();
        var cookie = getRefreshTokenCookie(loginResponse);

        // Refresh (expecting success)
        var refreshResponse = restClient.post()
                .uri("/api/auth/refresh")
                .cookie(cookie.getKey(), cookie.getValue())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refreshResponse.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(refreshResponse.getHeaders().headerNames()).contains("set-cookie");

        var expectedRespnse = String.format("""
            {
                "data": {
                    "user": {
                        "email": "%s",
                        "lastName": "User",
                        "firstName": "Test",
                        "authorities": []
                    },
                    "accessToken": "{{STRING}}"
                },
                "error": null
            }
        """.formatted(email));

        JsonAssertionBuilder.assertThatJson(refreshResponse.getBody())
                .matchesContract(expectedRespnse);

        cookie = getRefreshTokenCookie(refreshResponse);

        // logout
        var logoutResponse = restClient.post()
                .uri("/api/auth/logout")
                .cookie(cookie.getKey(), cookie.getValue())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {/* Don't Care */})
                .toEntity(String.class);

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(logoutResponse.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(logoutResponse.getHeaders().headerNames()).contains("set-cookie");

        expectedRespnse = """
            {
                "data": "You have been logged out",
                "error": null
            }
        """;

        JsonAssertionBuilder.assertThatJson(logoutResponse.getBody())
                .matchesContract(expectedRespnse);

        cookie = getRefreshTokenCookie(logoutResponse);

        refreshExpectingBadTokenError(cookie.getValue());
    }

    private <T> Pair<String, String> getRefreshTokenCookie(ResponseEntity<T> response) throws Exception {
        var setCookieHheaders = response.getHeaders().get("set-cookie");
        assertThat(setCookieHheaders).hasSize(1);
        var refreshTokenCookie = setCookieHheaders.getFirst();
        var cookieName = refreshTokenCookie.split("=")[0];
        var cookieValue = refreshTokenCookie.split("=")[1];
        return Pair.of(cookieName, cookieValue);
    }

    private ResponseEntity<String> loginExpectingSuccess() {
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

        var expectedResponse = String.format("""
            {
                "data": {
                    "user": {
                        "email": "%s",
                        "lastName": "User",
                        "firstName": "Test",
                        "authorities": []
                    },
                    "accessToken": "{{STRING}}"
                },
                "error": null
            }
        """.formatted(email));

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedResponse);

        return response;
    }

    private void refreshExpectingBadTokenError(String badCookie) {
        var response = restClient.post()
                .uri("/api/auth/refresh")
                .cookie("refresh-token", badCookie)
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
                    "timestamp" : "{{TIMESTAMP?amount=10&unit=SECONDS}}",
                    "error" : "BadCredentialsException",
                    "message" : "{{STRING}}"
                }
            }
        """;

        JsonAssertionBuilder.assertThatJson(response.getBody())
                .matchesContract(expectedResponse);
    }
}
