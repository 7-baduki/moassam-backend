package com.moassam.support;

import com.moassam.auth.domain.RefreshToken;

import java.time.LocalDateTime;

public class RefreshTokenFixture {

    public static RefreshToken create() {
        return RefreshToken.create(
                1L,
                "test-refresh-token",
                LocalDateTime.now().plusDays(14)
        );
    }

    public static RefreshToken createExpired() {
        return RefreshToken.create(
                1L,
                "expired-refresh-token",
                LocalDateTime.now().minusDays(1)
        );
    }
}