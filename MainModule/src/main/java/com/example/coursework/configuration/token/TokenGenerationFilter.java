package com.example.coursework.configuration.token;

import com.example.coursework.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor

@Service
public class TokenGenerationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final BCryptPasswordEncoder encoder;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    @NotNull final HttpServletResponse response,
                                    @NotNull final FilterChain filterChain) throws ServletException, IOException {
        final String password = request.getParameter("password");

        final String username = request.getParameter("loginAuth");

        if (nonNull(username) && isNotBlank(username)) {
            final UserDetails userDetails = tokenService.loadUserByUsername(username);
            if (nonNull(userDetails)) {
                if (encoder.matches(password, userDetails.getPassword())) {
                    final String token = tokenService.createToken(userDetails);
                    response.addCookie(new Cookie("token", token));
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
