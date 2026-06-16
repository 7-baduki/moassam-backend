package com.moassam.auth.adapter.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

public class HttpOnlyCookie {

    private final String name;
    private final int maxAgeSeconds;
    private final boolean secure;
    private final String sameSite;
    private final String domain;

    public HttpOnlyCookie(String name, int maxAgeSeconds, boolean secure, String sameSite, String domain) {
        this.name = name;
        this.maxAgeSeconds = maxAgeSeconds;
        this.secure = secure;
        this.sameSite = sameSite;
        this.domain = domain;
    }

    public void add(HttpServletResponse response, String value) {
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie(value, maxAgeSeconds).toString());
    }

    public void clear(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie("", 0).toString());
    }

    private ResponseCookie createCookie(String value, int maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder builder =
                ResponseCookie.from(name, value)
                        .httpOnly(true)
                        .secure(secure)
                        .path("/")
                        .maxAge(maxAgeSeconds)
                        .sameSite(sameSite);

        if (StringUtils.hasText(domain)) {
            builder.domain(domain);
        }

        return builder.build();
    }
}
