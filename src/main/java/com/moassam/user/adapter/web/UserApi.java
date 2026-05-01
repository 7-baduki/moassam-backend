package com.moassam.user.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.shared.web.SuccessResponse;
import com.moassam.user.adapter.web.dto.ProfileResponse;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserApi {

    private final UserProfile userProfile;

    @RequireAuth
    @PatchMapping("/me/nickname")
    public SuccessResponse<ProfileResponse> updateNickname(
            @CurrentUserId Long userId,
            @RequestBody String nickname
    ) {
        User user = userProfile.updateNickname(userId, nickname);

        return SuccessResponse.of(ProfileResponse.from(user));
    }
}