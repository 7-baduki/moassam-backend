package com.moassam.support;

import com.moassam.user.domain.Provider;
import com.moassam.user.domain.User;
import com.moassam.user.domain.UserRegisterRequest;

public class UserFixture {

    public static User create() {
        return User.register(new UserRegisterRequest(
                Provider.KAKAO,
                "kakao-12345",
                "moassam@kakao.com",
                "모아쌤",
                "https://kakaocdn.net/profile/moassam.jpg"
        ));
    }

    public static User createWithNickname(String nickname) {
        User user = create();
        user.updateNickname(nickname);
        return user;
    }

    public static User createWithdrawn() {
        User user = create();
        user.withdraw();
        return user;
    }
}