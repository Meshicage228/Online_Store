package com.example.coursework.configuration.token;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor

@Service
public class TokenGenerationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final BCryptPasswordEncoder encoder;
    private final UsersClient client;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String password = request.getParameter("password");

        String username = request.getParameter("loginAuth");

        if (nonNull(username) && isNotBlank(username)) {
            UserDetails userDetails = tokenService.loadUserByUsername(username);
            if(nonNull(userDetails)) {
                if (encoder.matches(password, userDetails.getPassword())) {
                    String token = tokenService.createToken(userDetails);
                    response.setHeader("Authorization", token);
                    response.addCookie(new Cookie("Authorization", token));
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
