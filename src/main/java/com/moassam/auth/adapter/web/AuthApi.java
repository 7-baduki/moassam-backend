package com.moassam.auth.adapter.web;

import com.moassam.auth.adapter.security.cookie.RefreshTokenCookie;
import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.auth.adapter.web.dto.TokenRefreshResponse;
import com.moassam.auth.application.provided.Auth;
import com.moassam.shared.web.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthApi {

    private final Auth auth;
    private final RefreshTokenCookie refreshTokenCookie;

    @PostMapping("/refresh")
    public SuccessResponse<TokenRefreshResponse> refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        String accessToken = auth.refresh(refreshToken);

        return SuccessResponse.of(new TokenRefreshResponse(accessToken));
    }

    @RequireAuth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(
            @CurrentUserId Long userId,
            HttpServletResponse response
    ) {
        auth.logout(userId);

        refreshTokenCookie.clear(response);
    }

    @RequireAuth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/withdraw")
    public void withdraw(
            @CurrentUserId Long userId,
            HttpServletResponse response
    ) {
        auth.withdraw(userId);

        refreshTokenCookie.clear(response);
    }
}