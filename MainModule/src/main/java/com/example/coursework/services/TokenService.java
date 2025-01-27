package com.example.coursework.services;

import com.example.coursework.dto.user.CurrentUser;
import com.example.coursework.domain.Role;
import com.example.coursework.dto.user.UserCard;
import com.example.coursework.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService implements UserDetailsService {
    private final UserRepository repos;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repos.findByName(username).orElseThrow(() -> new RuntimeException());
    }

    @Value(value = "${project.secretKey}")
    private String keyValue;

    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(keyValue.getBytes());
    }

    public String createToken(UserDetails userDetails) {
        var user = (CurrentUser) userDetails;
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        String roles = authorities.stream()
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

    public Authentication fromToken(String token) {
        JwtParser parser = Jwts.parser()
                .setSigningKey(secretKey)
                .build();

        Claims payload = (Claims) parser.parse(token).getPayload();


        var user_id = (String) payload.get("id");
        var roles = (String) payload.get("roles");
        var avatar = (byte[]) payload.get("avatar");
        var card = (UserCard) payload.get("card");

        CurrentUser user = CurrentUser.builder()
                .id(UUID.fromString(user_id))
                .role(Role.valueOf(roles))
                .card(card)
                .avatar(avatar)
                .build();

        List<SimpleGrantedAuthority> list = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(user, null, list);
    }
}
