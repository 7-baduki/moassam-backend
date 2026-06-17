package com.moassam.admin.application.required;

import com.moassam.admin.domain.AdminRefreshToken;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AdminRefreshTokenRepository extends Repository<AdminRefreshToken, Long> {

    AdminRefreshToken save(AdminRefreshToken refreshToken);

    Optional<AdminRefreshToken> findByToken(String token);

    Optional<AdminRefreshToken> findByAdminAccountId(Long adminAccountId);

    void deleteByAdminAccountId(Long adminAccountId);
}