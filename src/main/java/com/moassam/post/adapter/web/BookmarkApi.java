package com.moassam.post.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.post.application.provided.bookmark.BookmarkRegister;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}")
@RestController
public class BookmarkApi {

    private final BookmarkRegister bookmarkRegister;

    @RequireAuth
    @PostMapping("/bookmarks")
    public SuccessResponse<Void> bookmark(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        bookmarkRegister.bookmark(userId, postId);

        return SuccessResponse.of(null);
    }

    @RequireAuth
    @DeleteMapping("/bookmarks")
    public SuccessResponse<Void> unbookmark(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        bookmarkRegister.unbookmark(userId, postId);

        return SuccessResponse.of(null);
    }
}
