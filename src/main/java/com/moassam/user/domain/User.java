package com.moassam.user.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private Long id;
    private Provider provider;
    private String providerId;
    private String email;
    private String nickname;

    private User(Provider provider, String providerId, String email, String nickname) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
    }

    public static User register(Provider provider, String providerId, String email, String nickname) {
        return new User(provider, providerId, email, nickname);
    }
}
