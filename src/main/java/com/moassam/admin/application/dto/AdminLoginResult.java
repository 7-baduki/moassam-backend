package com.moassam.admin.application.dto;

public record AdminLoginResult(
        String accessToken,
        String refreshToken
) {
}
