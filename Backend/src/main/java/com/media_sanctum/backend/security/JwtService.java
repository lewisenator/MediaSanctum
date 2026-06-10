package com.media_sanctum.backend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.config.MediaSanctumJwtConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class JwtService {

    private final MediaSanctumJwtConfig jwtConfig;
    private final JWSSigner jwsSigner;
    private final JWSVerifier jwsVerifier;
    private final ObjectMapper objectMapper;

    public JwtService(
            MediaSanctumJwtConfig jwtConfig,
            ObjectMapper objectMapper
    ) throws JOSEException {
        this.jwtConfig = jwtConfig;
        this.jwsSigner = new MACSigner(jwtConfig.secret());
        this.jwsVerifier = new MACVerifier(jwtConfig.secret());
        this.objectMapper = objectMapper;
    }

    public String generateAccessToken(String userId) {
        var tokenPayload = TokenPayload.builder()
                .tokenType(TokenType.ACCESS)
                .userId(userId)
                .build();
        return generateJwtToken(tokenPayload, jwtConfig.accessExp().getDuration());
    }

    public String generateRefreshToken(String userId) {
        var tokenPayload = TokenPayload.builder()
                .tokenType(TokenType.REFRESH)
                .userId(userId)
                .build();
        return generateJwtToken(tokenPayload, jwtConfig.refreshExp().getDuration());
    }

    protected String generateJwtToken(TokenPayload tokenPayload, Duration ttl) throws AuthenticationException {
        tokenPayload.setExp(Instant.now().plus(ttl));
        try {
            String stringPayload = objectMapper.writeValueAsString(tokenPayload);
            var header = new JWSHeader(JWSAlgorithm.HS256);
            var payload = new Payload(stringPayload);
            var jwsObject = new JWSObject(header, payload);
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        } catch (JOSEException | JsonProcessingException e) {
            var message = String.format("Failed to generate JWT Token. %s", e.getMessage());
            log.error(message, e);
            throw new InternalAuthenticationServiceException(message, e);
        }
    }

    public TokenPayload verifyJwtToken(String token) throws AuthenticationException {
        try {
            var jwsObject = JWSObject.parse(token);
            if (!jwsObject.verify(jwsVerifier)) {
                throw new BadCredentialsException("Invalid token");
            }

            var stringPayload = jwsObject.getPayload().toString();
            var tokenPayload = objectMapper.readValue(stringPayload, TokenPayload.class);
            if (tokenPayload.getExp().isBefore(Instant.now())) {
                var message = String.format("Expired token for user: %s", tokenPayload.getUserId());
                log.warn(message);
            }
            return tokenPayload;
        } catch (JOSEException | JsonProcessingException | ParseException e) {
            var message = String.format("Failed to validate JWT Token. %s", e.getMessage());
            log.error(message, e);
            throw new BadCredentialsException(message, e);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenPayload {
        private String userId;
        private Instant exp;
        private TokenType tokenType;
    }

    public enum TokenType {
        ACCESS, REFRESH
    }
}
