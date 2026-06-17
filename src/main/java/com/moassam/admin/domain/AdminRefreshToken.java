package com.moassam.admin.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminRefreshToken extends BaseEntity {

    private Long id;
    private Long adminAccountId;
    private String token;
    private LocalDateTime expiresAt;

    public static AdminRefreshToken create(Long adminAccountId, String
            token, LocalDateTime expiresAt) {
        AdminRefreshToken refreshToken = new AdminRefreshToken();
        refreshToken.adminAccountId = adminAccountId;
        refreshToken.token = token;
        refreshToken.expiresAt = expiresAt;
        return refreshToken;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void rotate(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}