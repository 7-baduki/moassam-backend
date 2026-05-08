package com.moassam.auth.adapter.security.oauth;

import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import com.moassam.auth.application.required.RefreshTokenRepository;
import com.moassam.auth.application.required.TokenProvider;
import com.moassam.auth.domain.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpOnlyCookie refreshTokenCookie;
    private final HttpOnlyCookie accessTokenCookie;

    @Value("${app.oauth.success-redirect-uri}")
    private String redirectUri;

    public OAuth2SuccessHandler(
            TokenProvider tokenProvider,
            RefreshTokenRepository refreshTokenRepository,
            @Qualifier("refreshTokenCookie") HttpOnlyCookie refreshTokenCookie,
            @Qualifier("accessTokenCookie") HttpOnlyCookie accessTokenCookie
    ) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenCookie = refreshTokenCookie;
        this.accessTokenCookie = accessTokenCookie;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        SocialUser socialUser = (SocialUser) authentication.getPrincipal();
        Long userId = socialUser.getUserId();

        String accessToken = tokenProvider.generateAccessToken(userId);
        String refreshToken = issueRefreshToken(userId);

        accessTokenCookie.add(response, accessToken);
        refreshTokenCookie.add(response, refreshToken);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    private String issueRefreshToken(Long userId) {
        String token = tokenProvider.generateRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.create(userId, token, tokenProvider.getRefreshTokenExpiresAt())
        );

        return token;
    }
}