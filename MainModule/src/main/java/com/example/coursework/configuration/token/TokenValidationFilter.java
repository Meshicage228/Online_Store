package com.example.coursework.configuration.token;

import com.example.coursework.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TokenValidationFilter extends OncePerRequestFilter {
    private static final String TOKEN_NAME = "token";
    private final TokenService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(TOKEN_NAME))
                .findFirst()
                .map(Cookie::getValue)
                .ifPresent(cookie -> {
                    Authentication authentication = service.fromToken(cookie);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

        filterChain.doFilter(request,response);
    }
}
