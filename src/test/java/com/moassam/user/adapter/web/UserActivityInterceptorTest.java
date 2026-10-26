package com.moassam.user.adapter.web;

import com.moassam.user.application.UserActivityRecorder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class UserActivityInterceptorTest {

    private final UserActivityRecorder recorder = mock(UserActivityRecorder.class);
    private final UserActivityInterceptor interceptor = new UserActivityInterceptor(recorder);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatedBusinessErrorStillCountsAsActiveButNotFeatureUse() throws Exception {
        authenticateUser(10L);
        MockHttpServletRequest request = request("POST", "/api/v1/observations");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(400);

        interceptor.preHandle(request, response, new Object());
        interceptor.afterCompletion(request, response, new Object(), new IllegalArgumentException());

        verify(recorder).recordActive(10L);
        verifyNoMoreInteractions(recorder);
    }

    @Test
    void successfulObservationCreationLeavesCommittedCreationCountingToPersistenceLayer() throws Exception {
        authenticateUser(11L);
        MockHttpServletRequest request = request("POST", "/api/v1/observations");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        interceptor.preHandle(request, response, new Object());
        interceptor.afterCompletion(request, response, new Object(), null);

        verify(recorder).recordActive(11L);
        verifyNoMoreInteractions(recorder);
    }

    @Test
    void adminPathIsExcludedFromUserActivity() throws Exception {
        authenticateUser(12L);
        MockHttpServletRequest request = request("GET", "/api/v1/admin/dashboard");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.preHandle(request, response, new Object());
        interceptor.afterCompletion(request, response, new Object(), null);

        verify(recorder, never()).recordActive(12L);
    }

    private void authenticateUser(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_TEACHER"))
                )
        );
    }

    private MockHttpServletRequest request(String method, String uri) {
        return new MockHttpServletRequest(method, uri);
    }
}
