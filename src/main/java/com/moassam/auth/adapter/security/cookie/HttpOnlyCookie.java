package com.moassam.auth.adapter.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class HttpOnlyCookie {

    private final String name;
    private final int maxAgeSeconds;
    private final boolean secure;
    private final String sameSite;

    public HttpOnlyCookie(String name, int maxAgeSeconds, boolean secure, String sameSite) {
        this.name = name;
        this.maxAgeSeconds = maxAgeSeconds;
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public void add(HttpServletResponse response, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clear(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}