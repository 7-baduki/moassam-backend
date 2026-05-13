package com.moassam.user.application;

import com.moassam.observation.application.provided.ObservationStatsFinder;
import com.moassam.post.application.provided.bookmark.BookmarkFinder;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.Post;
import com.moassam.shared.exception.BusinessException;
import com.moassam.support.UserFixture;
import com.moassam.user.application.dto.MyActivityCountsResponse;
import com.moassam.user.application.dto.MyBookmarkedResponse;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import com.moassam.user.exception.UserErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObservationStatsFinder observationStatsFinder;

    @Mock
    private BookmarkFinder bookmarkFinder;

    @InjectMocks
    private UserService userService;

    @Test
    void getProfile() {
        User user = UserFixture.create();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User result = userService.getProfile(1L);

        assertThat(result.getEmail()).isEqualTo("moassam@kakao.com");
        assertThat(result.getNickname()).isEqualTo("모아쌤");
        assertThat(result.getProfileImageUrl()).isEqualTo("https://kakaocdn.net/profile/moassam.jpg");
    }

    @Test
    void getProfile_userNotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    void updateNickname() {
        User user = UserFixture.create();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User result = userService.updateNickname(1L, "새닉네임");

        assertThat(result.getNickname()).isEqualTo("새닉네임");
    }

    @Test
    void updateNickname_userNotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateNickname(999L, "새닉네임"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getMyBookmarkedPosts() {
        Post post = Post.create(
                99L,
                "title",
                "content",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        PageRequest pageable = PageRequest.of(0, 10);

        given(bookmarkFinder.getBookmarkedPosts(1L, 0, 10))
                .willReturn(new PageImpl<>(List.of(post), pageable, 1));

        Page<MyBookmarkedResponse> result = userService.getMyBookmarkedPosts(1L, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().postId()).isEqualTo(post.getId());
        assertThat(result.getContent().getFirst().title()).isEqualTo("title");
        assertThat(result.getContent().getFirst().category()).isEqualTo(Category.FREE);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getMyActivityCounts() {
        given(observationStatsFinder.countByUserId(1L)).willReturn(5L);
        given(bookmarkFinder.countByUserId(1L)).willReturn(3L);

        MyActivityCountsResponse result = userService.getMyActivityCounts(1L);

        assertThat(result.observationCount()).isEqualTo(5L);
        assertThat(result.bookmarkedPostCount()).isEqualTo(3L);
    }
}