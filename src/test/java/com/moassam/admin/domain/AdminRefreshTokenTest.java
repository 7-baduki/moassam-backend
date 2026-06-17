package com.moassam.admin.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AdminRefreshTokenTest {

    @Test
    void create() {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);

        AdminRefreshToken refreshToken = AdminRefreshToken.create(
                1L,
                "admin-refresh-token",
                expiresAt
        );

        assertThat(refreshToken.getAdminAccountId()).isEqualTo(1L);
        assertThat(refreshToken.getToken()).isEqualTo("admin-refresh-token");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void isExpired_false() {
        AdminRefreshToken refreshToken = AdminRefreshToken.create(
                1L,
                "admin-refresh-token",
                LocalDateTime.now().plusDays(14)
        );

        assertThat(refreshToken.isExpired()).isFalse();
    }

    @Test
    void isExpired_true() {
        AdminRefreshToken refreshToken = AdminRefreshToken.create(
                1L,
                "expired-admin-refresh-token",
                LocalDateTime.now().minusDays(1)
        );

        assertThat(refreshToken.isExpired()).isTrue();
    }

    @Test
    void rotate() {
        AdminRefreshToken refreshToken = AdminRefreshToken.create(
                1L,
                "old-refresh-token",
                LocalDateTime.now().plusDays(1)
        );
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(14);

        refreshToken.rotate("new-refresh-token", newExpiresAt);

        assertThat(refreshToken.getToken()).isEqualTo("new-refresh-token");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(newExpiresAt);
    }
}
