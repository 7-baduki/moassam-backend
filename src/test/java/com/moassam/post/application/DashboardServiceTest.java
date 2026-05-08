package com.moassam.post.application;

import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import com.moassam.post.domain.post.*;
import com.moassam.support.UserFixture;
import com.moassam.support.post.PostFixture;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class DashboardServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final DashboardService dashboardService = new DashboardService(postRepository, userRepository);

    @Test
    void getMoabangDashboard() {
        Post post = PostFixture.createMoabangPost(1L);

        User user = UserFixture.createWithNickname("햇살선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findMoabangDashboard(
                eq(Category.MOABANG),
                eq(PostAge.AGE_3),
                eq(ResourceType.JOURNAL),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(post)));

        given(userRepository.findAllByIdIn(List.of(1L)))
                .willReturn(List.of(user));

        Page<MoabangDashboardDetail> result = dashboardService.getMoabangDashboard(
                1L,
                PostAge.AGE_3,
                ResourceType.JOURNAL,
                0,
                9
        );

        MoabangDashboardDetail detail = result.getContent().get(0);

        assertThat(detail.postId()).isEqualTo(1L);
        assertThat(detail.title()).isEqualTo(post.getTitle());
        assertThat(detail.authorNickName()).isEqualTo("햇살선생님");
        assertThat(detail.thumbnailUrl()).isNotBlank();
        assertThat(detail.postAge()).isEqualTo(PostAge.AGE_3);
        assertThat(detail.resourceType()).isEqualTo(ResourceType.JOURNAL);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        then(postRepository).should().findMoabangDashboard(
                eq(Category.MOABANG),
                eq(PostAge.AGE_3),
                eq(ResourceType.JOURNAL),
                pageableCaptor.capture()
        );

        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(9);

        then(userRepository).should().findAllByIdIn(List.of(1L));
    }

    @Test
    void getFreeDashboard() {
        Post post = PostFixture.createFreePost(1L);

        User user = UserFixture.createWithNickname("바다선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findFreeDashboard(
                eq(Category.FREE),
                eq(HeadTag.QUESTION),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(post)));

        given(userRepository.findAllByIdIn(List.of(1L)))
                .willReturn(List.of(user));

        Page<FreeDashboardDetail> result = dashboardService.getFreeDashboard(
                1L,
                HeadTag.QUESTION,
                0,
                9
        );

        FreeDashboardDetail detail = result.getContent().get(0);

        assertThat(detail.postId()).isEqualTo(1L);
        assertThat(detail.title()).isEqualTo(post.getTitle());
        assertThat(detail.authorNickName()).isEqualTo("바다선생님");
        assertThat(detail.contentPreview()).isNotBlank();
        assertThat(detail.headTag()).isEqualTo(HeadTag.QUESTION);

        then(userRepository).should().findAllByIdIn(List.of(1L));
    }
}
