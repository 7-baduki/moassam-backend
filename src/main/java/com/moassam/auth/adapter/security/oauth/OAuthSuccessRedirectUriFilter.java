package com.moassam.auth.adapter.security.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuthSuccessRedirectUriFilter extends OncePerRequestFilter {

    public static final String SUCCESS_REDIRECT_URI_COOKIE_NAME = "oauth_success_redirect_uri";

    private static final String AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization/";
    private static final String SUCCESS_REDIRECT_URI_PARAM = "success_redirect_uri";

    private final Set<String> allowedSuccessRedirectUris;
    private final boolean secure;
    private final String sameSite;
    private final String domain;

    public OAuthSuccessRedirectUriFilter(
            @Value("${app.oauth.success-redirect-uris}") String successRedirectUris,
            @Value("${app.cookie.refresh-token.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite,
            @Value("${app.cookie.domain:}") String domain
    ) {
        this.allowedSuccessRedirectUris = Arrays.stream(successRedirectUris.split(","))
                .map(String::trim)
                .filter(uri -> !uri.isBlank())
                .collect(Collectors.toUnmodifiableSet());
        this.secure = secure;
        this.sameSite = sameSite;
        this.domain = domain;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isOAuthAuthorizationRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String successRedirectUri = request.getParameter(SUCCESS_REDIRECT_URI_PARAM);

        if (successRedirectUri == null || successRedirectUri.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!allowedSuccessRedirectUris.contains(successRedirectUri)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 Success Redirect URI");
            return;
        }

        addSuccessRedirectUriCookie(response, successRedirectUri);
        filterChain.doFilter(request, response);
    }

    private boolean isOAuthAuthorizationRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(AUTHORIZATION_REQUEST_BASE_URI);
    }

    private void addSuccessRedirectUriCookie(HttpServletResponse response, String successRedirectUri) {
        String encodedValue = URLEncoder.encode(successRedirectUri, StandardCharsets.UTF_8);

        ResponseCookie.ResponseCookieBuilder builder =
                ResponseCookie.from(SUCCESS_REDIRECT_URI_COOKIE_NAME, encodedValue)
                        .httpOnly(true)
                        .secure(secure)
                        .sameSite(sameSite)
                        .path("/")
                        .maxAge(Duration.ofMinutes(3));

        if (StringUtils.hasText(domain)) {
            builder.domain(domain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE,
                builder.build().toString());
    }
}
