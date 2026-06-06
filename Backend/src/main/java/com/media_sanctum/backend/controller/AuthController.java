package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.config.MediaSanctumJwtConfig;
import com.media_sanctum.backend.model.UserModel;
import com.media_sanctum.backend.resource.AuthResponse;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import com.media_sanctum.backend.resource.LoginRequest;
import com.media_sanctum.backend.resource.UserResponse;
import com.media_sanctum.backend.security.JwtService;
import com.media_sanctum.backend.service.UserService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh-token";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final MediaSanctumConfig mediaSanctumConfig;
    private final MediaSanctumJwtConfig jwtConfig;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService,
            MediaSanctumConfig mediaSanctumConfig,
            MediaSanctumJwtConfig jwtConfig
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.mediaSanctumConfig = mediaSanctumConfig;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword());

        authenticationManager.authenticate(authentication);

        var userDetails = userService.loadUserByUsername(loginRequest.getEmail());
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        var user = userService.getUserModelByEmail(userDetails.getUsername()).orElseThrow();

        return buildAuthResponseEntity(user, authorities);
    }

    @PostMapping("/logout")
    public ResponseEntity<DataResponse<String>> logout() {
        var emptyCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, null)
                .httpOnly(true)
                .secure(mediaSanctumConfig.cookiesSecure())
                .path("/api/auth")
                .maxAge(jwtConfig.refreshExp().getDuration())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, emptyCookie.toString())
                .body(DataResponse.data("You have been logged out"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataResponse<AuthResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            var error = ErrorResponse.builder()
                    .error("INVALID_REFRESH_TOKEN")
                    .message("Invalid refresh token")
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<AuthResponse> response = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        var tokenPayload = jwtService.verifyJwtToken(refreshToken);

        var maybeUser = userService.getUserModelById(tokenPayload.getUserId());
        if (maybeUser.isEmpty()) {
            var error = ErrorResponse.builder()
                    .error("INVALID_REFRESH_TOKEN")
                    .message("Invalid refresh token")
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<AuthResponse> response = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        var user = maybeUser.get();
        var userDetails = userService.loadUserByUsername(user.getEmail());
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return buildAuthResponseEntity(user, authorities);
    }

    private @NonNull ResponseEntity<DataResponse<AuthResponse>> buildAuthResponseEntity(
            UserModel user, List<@Nullable String> authorities) {
        var accessToken = jwtService.generateAccessToken(user.getId());
        var refreshToken = jwtService.generateRefreshToken(user.getId());

        var cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(mediaSanctumConfig.cookiesSecure())
                .path("/api/auth")
                .maxAge(jwtConfig.refreshExp().getDuration())
                .build();

        var userResponse = UserResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .authorities(authorities)
                .build();

        var loginResponse = AuthResponse.builder()
                .user(userResponse)
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(DataResponse.data(loginResponse));
    }
}
