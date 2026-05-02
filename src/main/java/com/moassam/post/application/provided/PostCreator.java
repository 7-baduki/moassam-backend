package com.moassam.post.application.provided;

import com.moassam.post.domain.PostCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostCreator {
    Long createPost(Long userId, PostCreateRequest request, List<MultipartFile> files, List<MultipartFile> editorImages);
}
