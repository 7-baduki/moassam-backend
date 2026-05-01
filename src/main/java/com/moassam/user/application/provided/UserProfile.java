package com.moassam.user.application.provided;

import com.moassam.user.domain.User;

public interface UserProfile {
    User updateNickname(Long userId, String nickname);
}