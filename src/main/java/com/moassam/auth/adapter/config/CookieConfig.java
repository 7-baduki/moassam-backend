package com.moassam.auth.adapter.config;

import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    public HttpOnlyCookie accessTokenCookie(
            @Value("${app.cookie.access-token.name}") String name,
            @Value("${app.cookie.access-token.max-age-ms}") long maxAgeMs,
            @Value("${app.cookie.access-token.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite,
            @Value("${app.cookie.domain:}") String domain
    ) {
        return new HttpOnlyCookie(name, (int) (maxAgeMs / 1000), secure,
                sameSite, domain);
    }


    @Bean
    public HttpOnlyCookie refreshTokenCookie(
            @Value("${app.cookie.refresh-token.name}") String name,
            @Value("${app.cookie.refresh-token.max-age-days}") int maxAgeDays,
            @Value("${app.cookie.refresh-token.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite,
            @Value("${app.cookie.domain:}") String domain
    ) {
        return new HttpOnlyCookie(name, maxAgeDays * 24 * 60 * 60, secure, sameSite, domain);
    }
}
