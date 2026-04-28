package com.moassam.user.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final String WITHDRAWN_NICKNAME = "탈퇴한 사용자";

    private Long id;

    private Provider provider;

    private String providerId;

    private String email;

    private String nickname;

    private String profileImageUrl;

    private LocalDateTime deletedAt;

    public static User register(UserRegisterRequest request) {
        User user = new User();

        user.provider = request.provider();
        user.providerId = request.providerId();
        user.email = request.email();
        user.nickname = request.nickname();
        user.profileImageUrl = request.profileImageUrl();

        return user;
    }

    public void withdraw() {
        if (isDeleted()) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }

        this.deletedAt = LocalDateTime.now();
        this.email = null;
        this.nickname = WITHDRAWN_NICKNAME;
        this.profileImageUrl = null;
    }

    public void rejoin(String email, String nickname, String profileImageUrl) {
        this.deletedAt = null;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}