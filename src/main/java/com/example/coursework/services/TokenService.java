/*
package com.example.coursework.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
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
*/
