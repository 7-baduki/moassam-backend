package com.moassam.auth.application.required;

import com.moassam.auth.domain.RefreshToken;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RefreshTokenRepository extends Repository<RefreshToken, Long> {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}