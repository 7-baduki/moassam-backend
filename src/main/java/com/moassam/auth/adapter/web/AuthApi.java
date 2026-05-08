package com.moassam.auth.adapter.web;

import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.auth.application.provided.Auth;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthApi {

    private final Auth auth;
    private final HttpOnlyCookie refreshTokenCookie;
    private final HttpOnlyCookie accessTokenCookie;

    public AuthApi(
            Auth auth,
            @Qualifier("refreshTokenCookie") HttpOnlyCookie refreshTokenCookie,
            @Qualifier("accessTokenCookie") HttpOnlyCookie accessTokenCookie
    ) {
        this.auth = auth;
        this.refreshTokenCookie = refreshTokenCookie;
        this.accessTokenCookie = accessTokenCookie;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/refresh")
    public void refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        String accessToken = auth.refresh(refreshToken);

        accessTokenCookie.add(response, accessToken);
    }

    @RequireAuth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(
            @CurrentUserId Long userId,
            HttpServletResponse response
    ) {
        auth.logout(userId);

        accessTokenCookie.clear(response);
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

        accessTokenCookie.clear(response);
        refreshTokenCookie.clear(response);
    }
}