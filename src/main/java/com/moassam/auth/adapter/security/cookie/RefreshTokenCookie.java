package com.moassam.auth.adapter.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookie {

    private final String name;
    private final int maxAgeSeconds;
    private final boolean secure;

    public RefreshTokenCookie(
            @Value("${app.cookie.refresh-token.name}") String name,
            @Value("${app.cookie.refresh-token.max-age-days}") int maxAgeDays,
            @Value("${app.cookie.refresh-token.secure}") boolean secure
    ) {
        this.name = name;
        this.maxAgeSeconds = maxAgeDays * 24 * 60 * 60;
        this.secure = secure;
    }

    public void add(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(name, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }

    public void clear(HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}