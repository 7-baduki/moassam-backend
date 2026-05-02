package com.moassam.post.application.provided.post;

import com.moassam.post.domain.post.PostUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostUpdater {
    Long updatePost(Long userId, Long postId, PostUpdateRequest request, List<MultipartFile> files, List<MultipartFile> editorImages);
}
