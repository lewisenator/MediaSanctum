package com.media_sanctum.backend.security;

import com.media_sanctum.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtRequestFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var header = request.getHeader("Authorization");
        var token = "";
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }
        if (StringUtils.isNoneEmpty(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var tokenDetails = jwtService.verifyJwtToken(token);
                var userId = tokenDetails.getUserId();
                var user = userService.getUserById(userId).orElseThrow();
                var userDetails = userService.loadUserByUsername(user.getEmail());
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception _) {}
        }
        filterChain.doFilter(request, response);
    }
}
