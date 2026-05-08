package com.moassam.user.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.post.domain.post.Category;
import com.moassam.shared.web.PageResponse;
import com.moassam.shared.web.SuccessResponse;
import com.moassam.user.adapter.web.dto.MyCommentResponse;
import com.moassam.user.adapter.web.dto.MyObservationResponse;
import com.moassam.user.adapter.web.dto.MyPostResponse;
import com.moassam.user.adapter.web.dto.ProfileResponse;
import com.moassam.user.application.provided.UserActivity;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserApi {

    private final UserProfile userProfile;
    private final UserActivity userActivity;

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

    @RequireAuth
    @GetMapping("/posts")
    public SuccessResponse<PageResponse<MyPostResponse>> getMyPosts(
            @CurrentUserId Long userId,
            @RequestParam Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MyPostResponse> result = userActivity.getMyPosts(userId, category, page, size);

        return SuccessResponse.of(PageResponse.of(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        ));
    }

    @RequireAuth
    @GetMapping("/comments")
    public SuccessResponse<PageResponse<MyCommentResponse>> getMyComments(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MyCommentResponse> result = userActivity.getMyComments(userId, page, size);

        return SuccessResponse.of(PageResponse.of(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        ));
    }

    @RequireAuth
    @GetMapping("/observations")
    public SuccessResponse<PageResponse<MyObservationResponse>> getMyObservations(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MyObservationResponse> result = userActivity.getMyObservations(userId, page, size);

        return SuccessResponse.of(PageResponse.of(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        ));
    }
}