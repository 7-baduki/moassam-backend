package com.moassam.post.application;

import com.moassam.post.application.required.PostFileRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import com.moassam.post.domain.post.*;
import com.moassam.support.UserFixture;
import com.moassam.support.post.PostFileFixture;
import com.moassam.support.post.PostFixture;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class DashboardServiceTest {

    private static final String ALL_INFANT_DEFAULT_THUMBNAIL_URL =
            "https://kr.object.ncloudstorage.com/moassam-storage/posts/dashboard/default_all_infant.png";

    private static final String AGE3_DEFAULT_THUMBNAIL_URL =
            "https://kr.object.ncloudstorage.com/moassam-storage/posts/dashboard/default_age3.png";

    private static final String AGE4_DEFAULT_THUMBNAIL_URL =
            "https://kr.object.ncloudstorage.com/moassam-storage/posts/dashboard/default_age4.png";

    private static final String AGE5_DEFAULT_THUMBNAIL_URL =
            "https://kr.object.ncloudstorage.com/moassam-storage/posts/dashboard/default_age5.png";

    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostFileRepository postFileRepository = mock(PostFileRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final DashboardService dashboardService = new DashboardService(postRepository, postFileRepository, userRepository);

    @Test
    void getMoabangDashboard() {
        Post post = PostFixture.createMoabangPost(1L);
        PostFile editorImage = PostFileFixture.createEditorImage(1L, post.getId());

        User user = UserFixture.createWithNickname("햇살선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findMoabangDashboard(
                eq(Category.MOABANG),
                eq(PostAge.AGE_3),
                eq(ResourceType.ACTIVITY),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(post)));

        given(userRepository.findAllByIdIn(List.of(1L)))
                .willReturn(List.of(user));

        given(postFileRepository.findAllByPostIdIn(List.of(post.getId())))
                .willReturn(List.of(editorImage));

        Page<MoabangDashboardDetail> result = dashboardService.getMoabangDashboard(
                PostAge.AGE_3,
                ResourceType.ACTIVITY,
                0,
                9
        );

        MoabangDashboardDetail detail = result.getContent().get(0);

        assertThat(detail.postId()).isEqualTo(1L);
        assertThat(detail.title()).isEqualTo(post.getTitle());
        assertThat(detail.authorNickName()).isEqualTo("햇살선생님");
        assertThat(detail.thumbnailUrl()).isEqualTo("https://example.com/image.png");
        assertThat(detail.postAge()).isEqualTo(PostAge.AGE_3);
        assertThat(detail.resourceType()).isEqualTo(ResourceType.ACTIVITY);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        then(postRepository).should().findMoabangDashboard(
                eq(Category.MOABANG),
                eq(PostAge.AGE_3),
                eq(ResourceType.ACTIVITY),
                pageableCaptor.capture()
        );

        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(9);

        then(userRepository).should().findAllByIdIn(List.of(1L));
        then(postFileRepository).should().findAllByPostIdIn(List.of(post.getId()));
    }

    @ParameterizedTest
    @MethodSource("postAgeDefaultThumbnailUrls")
    void getMoabangDashboardUsesPostAgeDefaultThumbnailWhenNoImageExists(
            PostAge postAge,
            String expectedThumbnailUrl
    ) {
        Post post = PostFixture.createMoabangPost(1L);
        ReflectionTestUtils.setField(post, "postAge", postAge);

        User user = UserFixture.createWithNickname("햇살선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findMoabangDashboard(
                eq(Category.MOABANG),
                eq(postAge),
                eq(ResourceType.ACTIVITY),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(post)));

        given(userRepository.findAllByIdIn(List.of(1L)))
                .willReturn(List.of(user));

        given(postFileRepository.findAllByPostIdIn(List.of(post.getId())))
                .willReturn(List.of());

        Page<MoabangDashboardDetail> result = dashboardService.getMoabangDashboard(
                postAge,
                ResourceType.ACTIVITY,
                0,
                9
        );

        MoabangDashboardDetail detail = result.getContent().get(0);

        assertThat(detail.thumbnailUrl()).isEqualTo(expectedThumbnailUrl);

        then(postFileRepository).should().findAllByPostIdIn(List.of(post.getId()));
    }

    @Test
    void searchMoabangUsesThumbnailFromEditorImage() {
        Post post = PostFixture.createMoabangPost(1L);
        PostFile editorImage = PostFileFixture.createEditorImage(1L, post.getId());

        User user = UserFixture.createWithNickname("햇살선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findAllByCategoryAndTitleContainingIgnoreCase(
                eq(Category.MOABANG),
                eq("활동지"),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(post)));

        given(userRepository.findAllByIdIn(List.of(1L)))
                .willReturn(List.of(user));

        given(postFileRepository.findAllByPostIdIn(List.of(post.getId())))
                .willReturn(List.of(editorImage));

        Page<MoabangDashboardDetail> result = dashboardService.searchMoabang(
                "활동지",
                0,
                9
        );

        MoabangDashboardDetail detail = result.getContent().get(0);

        assertThat(detail.thumbnailUrl()).isEqualTo("https://example.com/image.png");

        then(postFileRepository).should().findAllByPostIdIn(List.of(post.getId()));
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

    private static Stream<Arguments> postAgeDefaultThumbnailUrls() {
        return Stream.of(
                Arguments.of(PostAge.ALL, ALL_INFANT_DEFAULT_THUMBNAIL_URL),
                Arguments.of(PostAge.INFANT, ALL_INFANT_DEFAULT_THUMBNAIL_URL),
                Arguments.of(PostAge.AGE_3, AGE3_DEFAULT_THUMBNAIL_URL),
                Arguments.of(PostAge.AGE_4, AGE4_DEFAULT_THUMBNAIL_URL),
                Arguments.of(PostAge.AGE_5, AGE5_DEFAULT_THUMBNAIL_URL)
        );
    }
}
