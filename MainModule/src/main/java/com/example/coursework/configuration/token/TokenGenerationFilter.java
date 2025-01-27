package com.example.coursework.configuration.token;

import com.example.coursework.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenGenerationFilter extends OncePerRequestFilter {
    private static final String TOKEN_NAME = "token";
    private static final String PASS_PARAM = "password";
    private static final String AUTH_PARAM = "loginAuth";
    private final TokenService tokenService;
    private final BCryptPasswordEncoder encoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String password = request.getParameter(PASS_PARAM);

        String username = request.getParameter(AUTH_PARAM);

        Optional.ofNullable(username)
                .filter(StringUtils::isNotBlank)
                .map(tokenService::loadUserByUsername)
                .filter(userDetails -> encoder.matches(password, userDetails.getPassword()))
                .ifPresent(userDetails -> {
                    String token = tokenService.createToken(userDetails);
                    response.addCookie(new Cookie(TOKEN_NAME, token));
                });

        filterChain.doFilter(request, response);
    }
}
