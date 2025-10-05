package com.example.coursework.services;

import com.example.coursework.domain.Role;
import com.example.coursework.dto.user.CurrentUser;
import com.example.coursework.dto.user.UserCard;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService implements UserDetailsService {
    private final UserRepos repos;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return repos.findByName(username).orElseThrow(RuntimeException::new);
    }

    @Value(value = "${project.secretKey}")
    private String keyValue;

    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(keyValue.getBytes());
    }

    public String createToken(final UserDetails userDetails) {
        final var user = (CurrentUser) userDetails;
        final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        final String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder().subject(user.getUsername())
                .claim("username", user.getUsername())
                .claim("id", user.getId())
                .claim("roles", roles)
                .claim("card", user.getCard())
                .claim("avatar", user.getAvatar())
                .signWith(secretKey)
                .compact();
    }

    public Authentication fromToken(final String token) {
        final JwtParser parser = Jwts.parser()
                .setSigningKey(secretKey)
                .build();

        final Claims payload = (Claims) parser.parse(token).getPayload();

        final var user_id = (String) payload.get("id");
        final var roles = (String) payload.get("roles");
        final var avatar = (byte[]) payload.get("avatar");
        final var card = (UserCard) payload.get("card");

        final CurrentUser user = CurrentUser.builder()
                .id(UUID.fromString(user_id))
                .role(Role.valueOf(roles))
                .card(card)
                .avatar(avatar)
                .build();

        final List<SimpleGrantedAuthority> list = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(user, null, list);
    }
}
