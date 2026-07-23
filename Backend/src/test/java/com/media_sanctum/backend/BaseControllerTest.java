package com.media_sanctum.backend;

import com.media_sanctum.backend.config.FlywayTestConfig;
import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.resource.AuthResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.LoginRequest;
import com.media_sanctum.backend.security.AdminUserBootstrapService;
import com.media_sanctum.backend.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"ADMIN_EMAIL=admin@example.com",
				"ADMIN_PASSWORD=password",
				"SPRING_PROFILES_ACTIVE=local,test",
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

	private static final Set<HttpMethod> CSRF_EXEMPT_METHODS =
			Set.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE);

	protected RestClient restClient;

	private String csrfToken;

	protected RestClient.ResponseSpec.ErrorHandler doNothingErrorHandler = (_, _) -> {
		// Do nothing; allow tests to inspect the response instead of throwing an exception.
	};

	@BeforeEach
	public void setup() {
		bootstrapService.bootstrap();
		csrfToken = null;

		restClient = RestClient.builder()
				.baseUrl("http://localhost:" + port)
				.requestInterceptor(this::attachCsrfToken)
				.build();
	}

	/**
	 * Mirrors what a browser does automatically: attach the XSRF-TOKEN cookie/header pair to every
	 * state-changing request, fetching one first if this test hasn't obtained one yet.
	 */
	private ClientHttpResponse attachCsrfToken(
			HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		if (!CSRF_EXEMPT_METHODS.contains(request.getMethod())) {
			var token = ensureCsrfToken();
			var headers = request.getHeaders();
			headers.set("X-XSRF-TOKEN", token);
			var existingCookie = headers.getFirst(HttpHeaders.COOKIE);
			headers.set(HttpHeaders.COOKIE, existingCookie == null
					? "XSRF-TOKEN=" + token
					: existingCookie + "; XSRF-TOKEN=" + token);
		}
		return execution.execute(request, body);
	}

	private synchronized String ensureCsrfToken() {
		if (csrfToken == null) {
			var response = restClient.get()
					.uri("/api/actuator/health")
					.retrieve()
					.toBodilessEntity();
			var setCookieHeaders = response.getHeaders().get(HttpHeaders.SET_COOKIE);
			csrfToken = setCookieHeaders == null ? null : setCookieHeaders.stream()
					.filter(header -> header.startsWith("XSRF-TOKEN="))
					.map(header -> header.split(";", 2)[0].substring("XSRF-TOKEN=".length()))
					.findFirst()
					.orElse(null);
			if (csrfToken == null) {
				throw new IllegalStateException("No XSRF-TOKEN cookie returned from health check");
			}
		}
		return csrfToken;
	}

	@DynamicPropertySource
	public static void configureDynamicProperties(DynamicPropertyRegistry registry) {
		var projectPath = "/tmp/media-sanctum-test" + UUID.randomUUID();
		registry.add("media-sanctum.config-dir", () -> projectPath + "/config");
		registry.add("media-sanctum.data-dir", () -> projectPath + "/data");
		registry.add("media-sanctum.log.path", () -> projectPath + "/config/logs");
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
