package com.moassam.post.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.post.application.provided.postlike.PostLikeRegister;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}")
@RestController
public class PostLikeApi {

    private final PostLikeRegister postLikeRegister;

    @RequireAuth
    @PostMapping("/likes")
    public SuccessResponse<Void> like(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        postLikeRegister.like(userId, postId);

        return SuccessResponse.of(null);
    }

    @RequireAuth
    @DeleteMapping("/likes")
    public SuccessResponse<Void> unlike(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        postLikeRegister.unlike(userId, postId);
        return SuccessResponse.of(null);
    }
}
