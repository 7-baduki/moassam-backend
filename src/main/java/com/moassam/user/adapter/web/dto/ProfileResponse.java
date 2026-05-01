package com.moassam.user.adapter.web.dto;

import com.moassam.user.domain.User;

public record ProfileResponse(
        String email,
        String nickname,
        String profileImageUrl
) {
    public static ProfileResponse from(User user) {
        return new ProfileResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}
