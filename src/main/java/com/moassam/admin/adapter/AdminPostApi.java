package com.moassam.admin.adapter;

import com.moassam.admin.adapter.annotation.RequireSuperAdmin;
import com.moassam.admin.adapter.dto.AdminPostResponse;
import com.moassam.admin.application.AdminPostService;
import com.moassam.post.domain.post.Category;
import com.moassam.shared.web.PageResponse;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/posts")
@RestController
@RequireSuperAdmin
public class AdminPostApi {

    private final AdminPostService adminPostService;

    @GetMapping
    public SuccessResponse<PageResponse<AdminPostResponse>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ) {
        Page<AdminPostResponse> posts = adminPostService.getPosts(parseCategory(category), keyword, page, size)
                .map(post -> new AdminPostResponse(
                        post.postId(),
                        post.title(),
                        post.author(),
                        post.category(),
                        post.createdAt(),
                        post.viewCount()
                ));
        return SuccessResponse.of(PageResponse.of(
                posts.getContent(),
                page,
                size,
                posts.getTotalElements()
        ));
    }

    @DeleteMapping("/{postId}")
    public SuccessResponse<Void> deletePost(@PathVariable Long postId) {
        adminPostService.deletePost(postId);
        return SuccessResponse.of(null);
    }

    private Category parseCategory(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            return null;
        }

        try {
            return Category.valueOf(category.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
    }
}
