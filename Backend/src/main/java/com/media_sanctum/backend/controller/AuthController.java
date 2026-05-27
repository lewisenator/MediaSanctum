package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.entity.User;
import com.media_sanctum.backend.model.DataResponse;
import com.media_sanctum.backend.model.LoginRequest;
import com.media_sanctum.backend.model.AuthResponse;
import com.media_sanctum.backend.model.UserResponse;
import com.media_sanctum.backend.repository.UserRepository;
import com.media_sanctum.backend.security.JwtService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh-token";

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MediaSanctumConfig mediaSanctumConfig;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            JwtService jwtService,
            MediaSanctumConfig mediaSanctumConfig
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.mediaSanctumConfig = mediaSanctumConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        authenticationManager.authenticate(authentication);

        var userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        return buildAuthResponseEntity(user, authorities);
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataResponse<AuthResponse>> refresh(@CookieValue(name = REFRESH_COOKIE_NAME) String refreshToken) {
        var tokenPayload = jwtService.verifyJwtToken(refreshToken);

        var user = userRepository.findById(tokenPayload.getUserId()).orElseThrow();
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return buildAuthResponseEntity(user, authorities);
    }

    private @NonNull ResponseEntity<DataResponse<AuthResponse>> buildAuthResponseEntity(User user, List<@Nullable String> authorities) {
        var accessToken = jwtService.generateAccessToken(user.getId());
        var refreshToken = jwtService.generateRefreshToken(user.getId());

        var cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(mediaSanctumConfig.cookiesSecure())
                .path("/api/auth")
                .maxAge(JwtService.REFRESH_TOKEN_TTL)
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
