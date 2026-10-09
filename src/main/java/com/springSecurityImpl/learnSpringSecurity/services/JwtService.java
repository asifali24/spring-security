package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String secretKey;

    private SecretKey generateKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(User user){
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", Set.of("ADMIN","USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ 1000*60 ))
                .signWith(generateKey())
                .compact();
    }


    public UUID getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

//        return (UUID) claims.getId();
        return UUID.fromString(claims.getSubject());
    }
}
