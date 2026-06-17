package com.moassam.admin.application;

import com.moassam.admin.adapter.security.AdminTokenProvider;
import com.moassam.admin.application.dto.AdminLoginResult;
import com.moassam.admin.application.required.AdminAccountRepository;
import com.moassam.admin.application.required.AdminRefreshTokenRepository;
import com.moassam.admin.domain.AdminAccount;
import com.moassam.admin.domain.AdminRefreshToken;
import com.moassam.admin.exception.AdminAuthErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock
    private AdminAccountRepository adminAccountRepository;

    @Mock
    private AdminRefreshTokenRepository adminRefreshTokenRepository;

    @Mock
    private AdminTokenProvider adminTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminAuthService adminAuthService;

    @Test
    void login() {
        AdminAccount admin = superAdmin();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);

        given(adminAccountRepository.findByUsername("super-admin")).willReturn(Optional.of(admin));
        given(passwordEncoder.matches("password", "encoded-password")).willReturn(true);
        given(adminTokenProvider.generateAccessToken(1L)).willReturn("admin-access-token");
        given(adminTokenProvider.generateRefreshToken()).willReturn("admin-refresh-token");
        given(adminTokenProvider.getRefreshTokenExpiresAt()).willReturn(expiresAt);
        given(adminRefreshTokenRepository.findByAdminAccountId(1L)).willReturn(Optional.empty());

        AdminLoginResult result = adminAuthService.login("super-admin", "password");

        assertThat(result.accessToken()).isEqualTo("admin-access-token");
        assertThat(result.refreshToken()).isEqualTo("admin-refresh-token");
        assertThat(admin.getLastLoginAt()).isNotNull();

        verify(adminRefreshTokenRepository).save(org.mockito.ArgumentMatchers.argThat(token ->
                token.getAdminAccountId().equals(1L)
                        && token.getToken().equals("admin-refresh-token")
                        && token.getExpiresAt().equals(expiresAt)
        ));
    }

    @Test
    void login_existingRefreshToken_rotates() {
        AdminAccount admin = superAdmin();
        AdminRefreshToken storedToken = AdminRefreshToken.create(
                1L,
                "old-refresh-token",
                LocalDateTime.now().plusDays(1)
        );
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(14);

        given(adminAccountRepository.findByUsername("super-admin")).willReturn(Optional.of(admin));
        given(passwordEncoder.matches("password", "encoded-password")).willReturn(true);
        given(adminTokenProvider.generateAccessToken(1L)).willReturn("admin-access-token");
        given(adminTokenProvider.generateRefreshToken()).willReturn("new-refresh-token");
        given(adminTokenProvider.getRefreshTokenExpiresAt()).willReturn(newExpiresAt);
        given(adminRefreshTokenRepository.findByAdminAccountId(1L)).willReturn(Optional.of(storedToken));

        AdminLoginResult result = adminAuthService.login("super-admin", "password");

        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(storedToken.getToken()).isEqualTo("new-refresh-token");
        assertThat(storedToken.getExpiresAt()).isEqualTo(newExpiresAt);

        verify(adminRefreshTokenRepository).save(storedToken);
    }

    @Test
    void login_invalidUsername() {
        given(adminAccountRepository.findByUsername("wrong-admin")).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.login("wrong-admin", "password"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AdminAuthErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    void login_invalidPassword() {
        AdminAccount admin = superAdmin();

        given(adminAccountRepository.findByUsername("super-admin")).willReturn(Optional.of(admin));
        given(passwordEncoder.matches("wrong-password", "encoded-password")).willReturn(false);

        assertThatThrownBy(() -> adminAuthService.login("super-admin", "wrong-password"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AdminAuthErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    void login_disabledAdmin() {
        AdminAccount admin = superAdmin();
        ReflectionTestUtils.setField(admin, "enabled", false);

        given(adminAccountRepository.findByUsername("super-admin")).willReturn(Optional.of(admin));

        assertThatThrownBy(() -> adminAuthService.login("super-admin", "password"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AdminAuthErrorCode.ADMIN_DISABLED);
    }

    @Test
    void refresh() {
        AdminRefreshToken token = AdminRefreshToken.create(
                1L,
                "valid-refresh-token",
                LocalDateTime.now().plusDays(14)
        );

        given(adminRefreshTokenRepository.findByToken("valid-refresh-token")).willReturn(Optional.of(token));
        given(adminTokenProvider.generateAccessToken(1L)).willReturn("new-admin-access-token");

        String result = adminAuthService.refresh("valid-refresh-token");

        assertThat(result).isEqualTo("new-admin-access-token");
    }

    @Test
    void refresh_invalidToken() {
        given(adminRefreshTokenRepository.findByToken("invalid-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.refresh("invalid-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AdminAuthErrorCode.INVALID_TOKEN);
    }

    @Test
    void refresh_expiredToken() {
        AdminRefreshToken expired = AdminRefreshToken.create(
                1L,
                "expired-refresh-token",
                LocalDateTime.now().minusDays(1)
        );

        given(adminRefreshTokenRepository.findByToken("expired-refresh-token")).willReturn(Optional.of(expired));

        assertThatThrownBy(() -> adminAuthService.refresh("expired-refresh-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AdminAuthErrorCode.EXPIRED_TOKEN);
    }

    @Test
    void logout() {
        adminAuthService.logout(1L);

        verify(adminRefreshTokenRepository).deleteByAdminAccountId(1L);
    }

    private AdminAccount superAdmin() {
        AdminAccount admin = AdminAccount.createSuperAdmin("super-admin", "encoded-password");
        ReflectionTestUtils.setField(admin, "id", 1L);
        return admin;
    }
}