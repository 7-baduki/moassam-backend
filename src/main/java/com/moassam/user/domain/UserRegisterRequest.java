package com.moassam.user.domain;

public record UserRegisterRequest(
        Provider provider,
        String providerId,
        String email,
        String nickname,
        String profileImageUrl
) {
}