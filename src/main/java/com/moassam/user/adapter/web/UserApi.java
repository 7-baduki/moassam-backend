package com.moassam.user.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.shared.web.SuccessResponse;
import com.moassam.user.adapter.web.dto.ProfileResponse;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserApi {

    private final UserProfile userProfile;

    @RequireAuth
    @GetMapping("/profile")
    public SuccessResponse<ProfileResponse> getProfile(
            @CurrentUserId Long userId
    ) {
        User user = userProfile.getProfile(userId);
        return SuccessResponse.of(ProfileResponse.from(user));
    }

    @RequireAuth
    @PatchMapping("/profile")
    public SuccessResponse<ProfileResponse> updateNickname(
            @CurrentUserId Long userId,
            @RequestBody String nickname
    ) {
        User user = userProfile.updateNickname(userId, nickname);

        return SuccessResponse.of(ProfileResponse.from(user));
    }
}