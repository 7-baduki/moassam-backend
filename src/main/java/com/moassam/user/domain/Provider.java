package com.moassam.user.domain;

public enum Provider {
    KAKAO,
    GOOGLE,
    NAVER;

    public static Provider from(String registrationId) {
        try {
            return Provider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth provider: " + registrationId);
        }
    }
}
