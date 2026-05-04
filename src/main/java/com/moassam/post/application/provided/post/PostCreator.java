package com.moassam.post.application.provided.post;

import com.moassam.post.domain.post.PostCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostCreator {
    Long createPost(Long userId, PostCreateRequest request, List<MultipartFile> files, List<MultipartFile> editorImages);
}
