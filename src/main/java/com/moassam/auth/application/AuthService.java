package com.moassam.auth.application;

import com.moassam.auth.application.provided.Auth;
import com.moassam.auth.application.required.RefreshTokenRepository;
import com.moassam.auth.application.required.TokenProvider;
import com.moassam.auth.domain.RefreshToken;
import com.moassam.auth.exception.AuthErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService implements Auth {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public String refresh(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_TOKEN));

        if (storedToken.isExpired()) {
            throw new BusinessException(AuthErrorCode.EXPIRED_TOKEN);
        }

        String newAccessToken = tokenProvider.generateAccessToken(storedToken.getUserId());

        return newAccessToken;
    }

    @Override
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        user.withdraw();

        refreshTokenRepository.deleteByUserId(userId);
    }
}