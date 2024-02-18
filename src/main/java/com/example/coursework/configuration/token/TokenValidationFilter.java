package com.example.coursework.configuration.token;

import com.example.coursework.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class TokenValidationFilter extends OncePerRequestFilter {
    private final TokenService service;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> tokenFromCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .map(Cookie::getValue);
        if(tokenFromCookie.isPresent()){
            Authentication authentication = service.fromToken(tokenFromCookie.get());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);
    }
}
