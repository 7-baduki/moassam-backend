package com.moassam.auth.adapter.security.oauth;

import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import com.moassam.auth.application.required.RefreshTokenRepository;
import com.moassam.auth.application.required.TokenProvider;
import com.moassam.auth.domain.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpOnlyCookie refreshTokenCookie;
    private final Set<String> allowedSuccessRedirectUris;
    private final String defaultSuccessRedirectUri;
    private final boolean secure;
    private final String sameSite;

    public OAuth2SuccessHandler(
            TokenProvider tokenProvider,
            RefreshTokenRepository refreshTokenRepository,
            @Qualifier("refreshTokenCookie") HttpOnlyCookie refreshTokenCookie,
            @Value("${app.oauth.success-redirect-uris}") String successRedirectUris,
            @Value("${app.oauth.default-success-redirect-uri}") String defaultSuccessRedirectUri,
            @Value("${app.cookie.refresh-token.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite
    ) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenCookie = refreshTokenCookie;
        this.allowedSuccessRedirectUris = Arrays.stream(successRedirectUris.split(","))
                .map(String::trim)
                .filter(uri -> !uri.isBlank())
                .collect((Collectors.toUnmodifiableSet()));
        this.defaultSuccessRedirectUri = defaultSuccessRedirectUri;
        this.secure = secure;
        this.sameSite = sameSite;
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

        refreshTokenCookie.add(response, refreshToken);

        String redirectUri = createRedirectUri(request, accessToken);
        deleteSuccessRedirectUriCookie(response);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    private String createRedirectUri(HttpServletRequest request, String accessToken) {
        String successRedirectUri = defineSuccessRedirectUri(request);

        return UriComponentsBuilder.fromUriString(successRedirectUri)
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();
    }

    private String defineSuccessRedirectUri(HttpServletRequest request) {
        return findSuccessRedirectUriCookie(request)
                .filter(allowedSuccessRedirectUris::contains)
                .orElse(defaultSuccessRedirectUri);
    }

    private Optional<String> findSuccessRedirectUriCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> OAuthSuccessRedirectUriFilter.SUCCESS_REDIRECT_URI_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .map(value -> URLDecoder.decode(value, StandardCharsets.UTF_8));
    }

    private void deleteSuccessRedirectUriCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(OAuthSuccessRedirectUriFilter.SUCCESS_REDIRECT_URI_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String issueRefreshToken(Long userId) {
        String token = tokenProvider.generateRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.create(userId, token, tokenProvider.getRefreshTokenExpiresAt())
        );

        return token;
    }
}