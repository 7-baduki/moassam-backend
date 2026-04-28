package com.moassam.user.domain;

import com.moassam.support.UserFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void register() {
        User user = UserFixture.create();

        assertThat(user.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(user.getProviderId()).isEqualTo("kakao-12345");
        assertThat(user.getEmail()).isEqualTo("moassam@kakao.com");
        assertThat(user.getNickname()).isEqualTo("모아쌤");
        assertThat(user.getProfileImageUrl()).isEqualTo("https://kakaocdn.net/profile/moassam.jpg");
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    void withdraw() {
        User user = UserFixture.create();

        user.withdraw();

        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getNickname()).isEqualTo("탈퇴한 사용자");
        assertThat(user.getProfileImageUrl()).isNull();
    }

    @Test
    void withdraw_alreadyDeleted_throwsException() {
        User user = UserFixture.createWithdrawn();

        assertThatThrownBy(user::withdraw)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 탈퇴한 회원입니다.");
    }

    @Test
    void rejoin() {
        User user = UserFixture.createWithdrawn();

        user.rejoin("new@kakao.com", "새닉네임", "https://new.image/url.jpg");

        assertThat(user.isDeleted()).isFalse();
        assertThat(user.getDeletedAt()).isNull();
        assertThat(user.getEmail()).isEqualTo("new@kakao.com");
        assertThat(user.getNickname()).isEqualTo("새닉네임");
        assertThat(user.getProfileImageUrl()).isEqualTo("https://new.image/url.jpg");
    }
}