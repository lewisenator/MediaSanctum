package com.media_sanctum.backend;

import com.media_sanctum.backend.config.FlywayTestConfig;
import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.resource.AuthResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.LoginRequest;
import com.media_sanctum.backend.security.AdminUserBootstrapService;
import com.media_sanctum.backend.service.UserService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.web.client.RestClient;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"DATA=/tmp/media-sanctum-test-data",
				"CONFIG=/tmp/media-sanctum-test-config",
				"ADMIN_EMAIL=admin@example.com",
				"ADMIN_PASSWORD=password",
				"JWT_SECRET=VUVnC7FxCBjGVgRrZdfLXD3GQjg/lkpptSAoQwibqGY=",
				"SPRING_PROFILES_ACTIVE=local",
				"spring.flyway.enabled=false",
		})
@Import(FlywayTestConfig.class)
public abstract class BaseControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	protected AdminUserBootstrapService bootstrapService;

	@Autowired
	protected UserService userService;

	protected RestClient restClient;

	protected String getAccessToken() {
		var authResponse = login();
		assertThat(authResponse).isNotNull();
		return authResponse.getAccessToken();
	}

	@BeforeEach
	public void setup() {
		bootstrapService.bootstrap();

		restClient = RestClient.builder()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@Test
	public void contextLoads() {
	}

	public AuthResponse login() {
		var createUser = CreateUserModel.builder()
				.email(UUID.randomUUID() + "@example.com")
				.password(UUID.randomUUID().toString())
				.firstName("Test")
				.lastName("User")
				.build();

		userService.createUser(createUser);

		var loginRequest = LoginRequest.builder()
				.email(createUser.getEmail())
				.password(createUser.getPassword())
				.build();

		var response = restClient.post()
				.uri("/api/auth/login")
				.body(loginRequest)
				.retrieve()
				.toEntity(new ParameterizedTypeReference<DataResponse<AuthResponse>>(){});

		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getData()).isNotNull();

		return response.getBody().getData();
	}
}
