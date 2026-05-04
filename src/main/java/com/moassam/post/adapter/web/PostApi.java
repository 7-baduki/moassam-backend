package com.moassam.post.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.post.adapter.web.dto.post.PostUpdateResponse;
import com.moassam.post.application.provided.post.PostDeleter;
import com.moassam.post.application.provided.post.PostUpdater;
import com.moassam.post.domain.post.PostCreateRequest;
import com.moassam.post.adapter.web.dto.post.PostCreateResponse;
import com.moassam.post.adapter.web.dto.post.PostDetailResponse;
import com.moassam.post.application.provided.post.PostCreator;
import com.moassam.post.application.provided.post.PostFinder;
import com.moassam.post.domain.post.PostDetail;
import com.moassam.post.domain.post.PostUpdateRequest;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApi {

    private final PostCreator postCreator;
    private final PostFinder postFinder;
    private final PostUpdater postUpdater;
    private final PostDeleter postDeleter;

    //게시글 등록
    @RequireAuth
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<PostCreateResponse> createPost(
            @CurrentUserId Long userId,
            @RequestPart("request") PostCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "editorImages", required = false) List<MultipartFile> editorImages
    ) {
        Long postId = postCreator.createPost(userId, request, files, editorImages);

        return SuccessResponse.of(new PostCreateResponse(postId));
    }


    //게시글 조회
    @RequireAuth
    @GetMapping("/{postId}")
    public SuccessResponse<PostDetailResponse> getPost(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        PostDetail postDetail = postFinder.getPost(userId, postId);

        return SuccessResponse.of(PostDetailResponse.from(postDetail));
    }

    //게시글 수정
    @RequireAuth
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<PostUpdateResponse> updatePost(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @RequestPart("request") PostUpdateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "editorImages", required = false) List<MultipartFile> editorImages
    ) {
        Long updatePostId = postUpdater.updatePost(userId, postId, request, files, editorImages);

        return SuccessResponse.of(new PostUpdateResponse(updatePostId));
    }

    //게시글 삭제
    @RequireAuth
    @DeleteMapping("/{postId}")
    public SuccessResponse<Void> deletePost(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        postDeleter.deletePost(userId, postId);

        return SuccessResponse.of(null);
    }
}
