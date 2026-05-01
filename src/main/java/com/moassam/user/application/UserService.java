package com.moassam.user.application;

import com.moassam.shared.exception.BusinessException;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import com.moassam.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService implements UserProfile {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateNickname(nickname);

        return user;
    }

    @Override
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}