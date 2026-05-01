package com.moassam.user.application.provided;

import com.moassam.user.domain.User;

public interface UserProfile {

    User getProfile(Long userId);

    User updateNickname(Long userId, String nickname);
}