package com.moassam.auth.domain;

import com.moassam.support.RefreshTokenFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void create() {
        RefreshToken refreshToken = RefreshTokenFixture.create();

        assertThat(refreshToken.getUserId()).isEqualTo(1L);
        assertThat(refreshToken.getToken()).isEqualTo("test-refresh-token");
        assertThat(refreshToken.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void isExpired_false() {
        RefreshToken refreshToken = RefreshTokenFixture.create();

        assertThat(refreshToken.isExpired()).isFalse();
    }

    @Test
    void isExpired_true() {
        RefreshToken refreshToken = RefreshTokenFixture.createExpired();

        assertThat(refreshToken.isExpired()).isTrue();
    }

    @Test
    void rotate() {
        RefreshToken refreshToken = RefreshTokenFixture.create();
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(30);

        refreshToken.rotate("new-token", newExpiresAt);

        assertThat(refreshToken.getToken()).isEqualTo("new-token");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(newExpiresAt);
    }
}