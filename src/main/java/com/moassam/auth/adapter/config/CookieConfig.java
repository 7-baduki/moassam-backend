package com.moassam.auth.adapter.config;

import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    public HttpOnlyCookie refreshTokenCookie(
            @Value("${app.cookie.refresh-token.name}") String name,
            @Value("${app.cookie.refresh-token.max-age-days}") int maxAgeDays,
            @Value("${app.cookie.refresh-token.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite
    ) {
        return new HttpOnlyCookie(name, maxAgeDays * 24 * 60 * 60, secure, sameSite);
    }

    @Bean
    public HttpOnlyCookie accessTokenCookie(
            @Value("${app.cookie.access-token.name}") String name,
            @Value("${app.cookie.access-token.max-age-seconds}") int maxAgeSeconds,
            @Value("${app.cookie.access-token.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite
    ) {
        return new HttpOnlyCookie(name, maxAgeSeconds, secure, sameSite);
    }
}