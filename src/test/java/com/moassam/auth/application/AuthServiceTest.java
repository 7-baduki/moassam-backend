package com.moassam.auth.application;

import com.moassam.auth.application.required.RefreshTokenRepository;
import com.moassam.auth.application.required.TokenProvider;
import com.moassam.auth.domain.RefreshToken;
import com.moassam.auth.exception.AuthErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.support.UserFixture;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void logout() {
        authService.logout(1L);

        verify(refreshTokenRepository).deleteByUserId(1L);
    }

    @Test
    void refresh() {
        RefreshToken token = RefreshToken.create(1L, "valid-token", LocalDateTime.now().plusDays(14));
        given(refreshTokenRepository.findByToken("valid-token")).willReturn(Optional.of(token));
        given(tokenProvider.generateAccessToken(1L)).willReturn("new-access-token");

        String result = authService.refresh("valid-token");

        assertThat(result).isEqualTo("new-access-token");
    }

    @Test
    void refresh_invalidToken() {
        given(refreshTokenRepository.findByToken("not-exist")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("not-exist"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }

    @Test
    void refresh_expiredToken() {
        RefreshToken expired = RefreshToken.create(1L, "expired-token", LocalDateTime.now().minusDays(1));
        given(refreshTokenRepository.findByToken("expired-token")).willReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("expired-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.EXPIRED_TOKEN);
    }

    @Test
    void withdraw() {
        User user = UserFixture.create();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        authService.withdraw(1L);

        assertThat(user.isDeleted()).isTrue();
        verify(refreshTokenRepository).deleteByUserId(1L);
    }

    @Test
    void withdraw_userNotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.withdraw(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.USER_NOT_FOUND);
    }
}