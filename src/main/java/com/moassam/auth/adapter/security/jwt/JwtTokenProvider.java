package com.moassam.auth.adapter.security.jwt;

import com.moassam.auth.application.required.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryDays;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry-ms}") long accessTokenExpiryMs,
            @Value("${jwt.refresh-token-expiry-days}") long refreshTokenExpiryDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryDays = refreshTokenExpiryDays;
    }

    @Override
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiryMs);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime getRefreshTokenExpiresAt() {
        return LocalDateTime.now().plusDays(refreshTokenExpiryDays);
    }
}