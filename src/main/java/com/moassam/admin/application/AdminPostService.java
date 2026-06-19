package com.moassam.admin.application;

import com.moassam.admin.application.dto.AdminPostSummary;
import com.moassam.admin.application.required.AdminPostQueryRepository;
import com.moassam.post.application.required.PostFileRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.Post;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.adapter.filestorage.FileStorage;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminPostService {

    private final AdminPostQueryRepository adminPostQueryRepository;
    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final FileStorage fileStorage;

    public Page<AdminPostSummary> getPosts(Category category, String keyword, int page, int size) {
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();

        return adminPostQueryRepository.findAdminPostSummaries(category, normalizedKeyword, PageRequest.of(page, size));
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        postFileRepository.findAllByPostId(postId)
                .forEach(file -> fileStorage.delete(file.getUrl()));

        postRepository.delete(post);
    }
}
