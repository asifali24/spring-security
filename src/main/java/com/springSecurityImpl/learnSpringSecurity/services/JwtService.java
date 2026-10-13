package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.constant.JwtConstant;
import com.springSecurityImpl.learnSpringSecurity.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${Jwt.key}")
    private String secretKey;

    private SecretKey generateKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(User user, Boolean isRefreshToken){
        long accessTokenDuration = JwtConstant.ACCESS_TOKEN_DURATION;
        long refreshTokenDuration = JwtConstant.REFRESH_TOKEN_DURATION;

        long duration = isRefreshToken ? refreshTokenDuration : accessTokenDuration;

        JwtBuilder token = Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duration));


        if(!isRefreshToken){
            token
                    .claim("email", user.getEmail())
                    .claim("roles", user.getRoles());
        }

        return token
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
