package com.moassam.auth.adapter.security.oauth;

import com.moassam.auth.adapter.security.cookie.RefreshTokenCookie;
import com.moassam.auth.application.required.RefreshTokenRepository;
import com.moassam.auth.application.required.TokenProvider;
import com.moassam.auth.domain.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenCookie refreshTokenCookie;

    @Value("${app.oauth.success-redirect-uri}")
    private String redirectUri;

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

        refreshTokenCookie.add(response, refreshToken);

        String targetUrl = buildRedirectUrl(accessToken);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String issueRefreshToken(Long userId) {
        String token = tokenProvider.generateRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.create(userId, token, tokenProvider.getRefreshTokenExpiresAt())
        );

        return token;
    }

    private String buildRedirectUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();
    }
}