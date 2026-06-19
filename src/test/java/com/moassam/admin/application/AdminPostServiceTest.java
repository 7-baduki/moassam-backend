package com.moassam.admin.application;

import com.moassam.admin.application.required.AdminPostQueryRepository;
import com.moassam.post.application.required.PostFileRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.post.*;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.adapter.filestorage.FileStorage;
import com.moassam.shared.exception.BusinessException;
import com.moassam.support.post.PostFixture;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

class AdminPostServiceTest {

    private final AdminPostQueryRepository adminPostQueryRepository = mock(AdminPostQueryRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostFileRepository postFileRepository = mock(PostFileRepository.class);
    private final FileStorage fileStorage = mock(FileStorage.class);

    private final AdminPostService adminPostService = new AdminPostService(
            adminPostQueryRepository,
            postRepository,
            postFileRepository,
            fileStorage
    );

    @Test
    void getPosts_trimKeyword() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        given(adminPostQueryRepository.findAdminPostSummaries(Category.FREE, "검색어", pageRequest))
                .willReturn(Page.empty(pageRequest));

        adminPostService.getPosts(Category.FREE, "  검색어  ", 1, 10);

        then(adminPostQueryRepository).should()
                .findAdminPostSummaries(Category.FREE, "검색어", pageRequest);
    }

    @Test
    void getPosts_blankKeyword_toNull() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        given(adminPostQueryRepository.findAdminPostSummaries(isNull(), isNull(), eq(pageRequest)))
                .willReturn(Page.empty(pageRequest));

        adminPostService.getPosts(null, "   ", 0, 20);

        then(adminPostQueryRepository).should()
                .findAdminPostSummaries(null, null, pageRequest);
    }

    @Test
    void deletePost() {
        Post post = PostFixture.createFreePost(1L);
        PostFile file = PostFile.create(
                1L,
                "test.pdf",
                "https://example.com/test.pdf",
                1024L,
                FileType.FILE
        );

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postFileRepository.findAllByPostId(1L)).willReturn(List.of(file));

        adminPostService.deletePost(1L);

        then(fileStorage).should().delete("https://example.com/test.pdf");
        then(postRepository).should().delete(post);
    }

    @Test
    void deletePost_notFound() {
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminPostService.deletePost(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);

        then(postFileRepository).should(never()).findAllByPostId(anyLong());
        then(postRepository).should(never()).delete(any());
    }
}
