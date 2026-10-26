package com.moassam.user.adapter.web;

import com.moassam.user.application.UserActivityRecorder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    private static final String API_PREFIX = "/api/v1/";
    private static final String ADMIN_PREFIX = "/api/v1/admin";
    private static final String AUTH_PREFIX = "/api/v1/auth";
    private static final String OBSERVATION_PREFIX = "/api/v1/observations";

    private final UserActivityRecorder recorder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long userId = currentUserId(request);
        if (userId != null) {
            recorder.recordActive(userId);
        }
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception
    ) {
        if (exception != null || response.getStatus() < 200 || response.getStatus() >= 300) {
            return;
        }

        Long userId = currentUserId(request);
        if (userId == null) {
            return;
        }

        String path = request.getRequestURI();
        if (path.startsWith(OBSERVATION_PREFIX)) {
            recordObservation(request, path, userId);
        } else if (isCommunityPath(path)) {
            recorder.recordCommunityUsed(userId);
        }
    }

    private void recordObservation(HttpServletRequest request, String path, Long userId) {
        boolean persistenceTrackedWrite = "POST".equals(request.getMethod())
                && (OBSERVATION_PREFIX.equals(path) || path.endsWith("/regenerate"));
        if (!persistenceTrackedWrite) {
            recorder.recordObservationUsed(userId);
        }
    }

    private boolean isCommunityPath(String path) {
        return path.startsWith("/api/v1/posts")
                || path.startsWith("/api/v1/comments")
                || path.startsWith("/api/v1/bookmarks")
                || path.startsWith("/api/v1/post-likes")
                || path.startsWith("/api/v1/dashboard");
    }

    private Long currentUserId(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith(API_PREFIX)
                || path.startsWith(ADMIN_PREFIX)
                || path.startsWith(AUTH_PREFIX)) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_TEACHER".equals(authority.getAuthority()))) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        try {
            return Long.valueOf(principal.toString());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
