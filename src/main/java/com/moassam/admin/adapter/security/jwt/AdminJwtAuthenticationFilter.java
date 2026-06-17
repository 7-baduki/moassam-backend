package com.moassam.admin.adapter.security.jwt;

import com.moassam.admin.adapter.security.AdminTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ADMIN_ACCESS_TOKEN_COOKIE_NAME = "adminAccessToken";
    private static final String ADMIN_ROLE = "ROLE_SUPER_ADMIN";
    private final AdminTokenProvider adminTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{
        authenticate(request);
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {
        extractTokenFromCookie(request)
                .map(adminTokenProvider::getAdminAccountId)
                .filter(adminAccountId -> adminAccountId != null)
                .ifPresent(this::setAuthentication);
    }

    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> ADMIN_ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void setAuthentication(Long adminAccountId) {
        UsernamePasswordAuthenticationToken authentication = new
                UsernamePasswordAuthenticationToken(
                adminAccountId,
                null,
                List.of(new SimpleGrantedAuthority(ADMIN_ROLE))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
