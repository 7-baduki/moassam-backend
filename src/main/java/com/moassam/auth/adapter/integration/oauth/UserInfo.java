package com.moassam.auth.adapter.integration.oauth;

import com.moassam.user.domain.Provider;

public record UserInfo(
        Provider provider,
        String providerId,
        String email,
        String nickname
) {
}