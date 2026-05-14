package com.moassam.auth.application;

import com.moassam.auth.application.provided.Auth;
import com.moassam.auth.application.required.RefreshTokenRepository;
import com.moassam.auth.application.required.TokenProvider;
import com.moassam.auth.domain.RefreshToken;
import com.moassam.auth.exception.AuthErrorCode;
import com.moassam.credit.application.required.CreditWalletRepository;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.application.required.ObservationSectionRepository;
import com.moassam.post.application.required.*;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService implements Auth {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostViewRepository postViewRepository;
    private final ObservationRepository observationRepository;
    private final ObservationSectionRepository observationSectionRepository;
    private final CreditWalletRepository creditWalletRepository;
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

        List<Long> commentPostIds = commentRepository.findPostIdsByUserId(userId);
        List<Long> likedPostIds = postLikeRepository.findPostIdsByUserId(userId);
        List<Long> observationIds = observationRepository.findIdsByUserId(userId);

        user.withdraw();

        commentRepository.deleteByUserId(userId);
        postLikeRepository.deleteByUserId(userId);

        if (!commentPostIds.isEmpty()) {
            postRepository.recalculateCommentCounts(commentPostIds);
        }

        if (!likedPostIds.isEmpty()) {
            postRepository.recalculateLikeCounts(likedPostIds);
        }

        if (!observationIds.isEmpty()) {
            observationSectionRepository.deleteByObservationIdIn(observationIds);
        }
        observationRepository.deleteByUserId(userId);
        bookmarkRepository.deleteByUserId(userId);
        postViewRepository.deleteByUserId(userId);
        creditWalletRepository.deleteByUserId(userId);
        refreshTokenRepository.deleteByUserId(userId);
    }
}