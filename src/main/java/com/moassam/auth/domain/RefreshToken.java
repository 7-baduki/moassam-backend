package com.moassam.auth.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    private Long id;

    private Long userId;

    private String token;

    private LocalDateTime expiresAt;

    public static RefreshToken create(Long userId, String token, LocalDateTime expiresAt) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.userId = userId;
        refreshToken.token = token;
        refreshToken.expiresAt = expiresAt;

        return refreshToken;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void rotate(String newToken, LocalDateTime newExpiresAt) {
        this.token = newToken;
        this.expiresAt = newExpiresAt;
    }
}