package com.example.userblservice.service.impl;

import com.example.userblservice.entity.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value(value = "${project.secretKey}")
    private String keyValue;

    private SecretKey secretKey;


    @PostConstruct
    public void init(){
        secretKey = Keys.hmacShaKeyFor(keyValue.getBytes());
    }
    public String createToken(UserDetails userDetails){
        var user = (UserEntity)userDetails;
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder().subject(user.getUsername())
                .claim("username", user.getUsername())
                .claim("id", user.getId())
                .claim("roles", roles)
                .signWith(secretKey)
                .compact();
    }
    public Authentication fromToken(String token){
        JwtParser parser = Jwts.parser()
                .setSigningKey(secretKey)
                .build();

        Claims payload = (Claims)parser.parse(token).getPayload();


        var id = (String)payload.get("id");
        var roles = (String)payload.get("roles");

        List<SimpleGrantedAuthority> list = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(UUID.fromString(id), null, list);
    }
}
