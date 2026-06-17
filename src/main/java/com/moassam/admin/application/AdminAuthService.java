package com.moassam.admin.application;

import com.moassam.admin.adapter.security.AdminTokenProvider;
import com.moassam.admin.application.dto.AdminLoginResult;
import com.moassam.admin.application.provided.AdminAuth;
import com.moassam.admin.application.required.AdminAccountRepository;
import com.moassam.admin.application.required.AdminRefreshTokenRepository;
import com.moassam.admin.domain.AdminAccount;
import com.moassam.admin.domain.AdminRefreshToken;
import com.moassam.admin.exception.AdminAuthErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminAuthService implements AdminAuth {

    private final AdminAccountRepository adminAccountRepository;
    private final AdminRefreshTokenRepository adminRefreshTokenRepository;
    private final AdminTokenProvider adminTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminLoginResult login(String username, String password) {
        AdminAccount admin = adminAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(AdminAuthErrorCode.INVALID_CREDENTIALS));

        if (admin.isDisabled()) {
            throw new BusinessException(AdminAuthErrorCode.ADMIN_DISABLED);
        }

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new BusinessException(AdminAuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = adminTokenProvider.generateAccessToken(admin.getId());
        String refreshToken = adminTokenProvider.generateRefreshToken();

        AdminRefreshToken storedToken = adminRefreshTokenRepository.findByAdminAccountId(admin.getId())
                .orElseGet(() -> AdminRefreshToken.create(
                        admin.getId(),
                        refreshToken,
                        adminTokenProvider.getRefreshTokenExpiresAt()
                ));

        storedToken.rotate(refreshToken, adminTokenProvider.getRefreshTokenExpiresAt());
        adminRefreshTokenRepository.save(storedToken);

        admin.recordLogin();

        return new AdminLoginResult(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public String refresh(String refreshToken) {
        AdminRefreshToken storedToken = adminRefreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(AdminAuthErrorCode.INVALID_TOKEN));

        if (storedToken.isExpired()) {
            throw new BusinessException(AdminAuthErrorCode.EXPIRED_TOKEN);
        }

        return adminTokenProvider.generateAccessToken(storedToken.getAdminAccountId());
    }

    @Override
    @Transactional
    public void logout(Long adminAccountId) {
        adminRefreshTokenRepository.deleteByAdminAccountId(adminAccountId);
    }
}