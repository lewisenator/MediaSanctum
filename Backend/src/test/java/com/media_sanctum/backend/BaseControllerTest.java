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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.web.client.RestClient;


import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"ADMIN_EMAIL=admin@example.com",
				"ADMIN_PASSWORD=password",
				"SPRING_PROFILES_ACTIVE=local",
				"spring.flyway.enabled=false",
		})
@Import(FlywayTestConfig.class)
public abstract class BaseControllerTest {

	public static final String DATA_CONTRACT = """
            {
                "data": %s,
                "error": null
            }
            """;

	@LocalServerPort
	private int port;

	@Autowired
	protected AdminUserBootstrapService bootstrapService;

	@Autowired
	protected UserService userService;

	protected RestClient restClient;

	@BeforeEach
	public void setup() {
		bootstrapService.bootstrap();

		restClient = RestClient.builder()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@DynamicPropertySource
	public static void configureDynamicProperties(DynamicPropertyRegistry registry) {
		var projectPath = "/tmp/media-sanctum-test" + UUID.randomUUID();
		registry.add("media-sanctum.config-dir", () -> projectPath + "/config");
		registry.add("media-sanctum.data-dir", () -> projectPath + "/data");
		registry.add("media-sanctum.jwt.secret", () -> {
			SecureRandom secureRandom = new SecureRandom();
			byte[] secretBytes = new byte[32]; // 256 bits
			secureRandom.nextBytes(secretBytes);
			return Base64.getEncoder().encodeToString(secretBytes);
		});
	}

	@Test
	public void contextLoads() {
	}

	protected String getAccessToken() {
		var authResponse = login();
		assertThat(authResponse).isNotNull();
		return authResponse.getAccessToken();
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
