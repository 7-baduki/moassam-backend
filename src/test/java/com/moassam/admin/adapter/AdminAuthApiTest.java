package com.moassam.admin.adapter;

import com.moassam.admin.adapter.dto.AdminLoginRequest;
import com.moassam.admin.application.dto.AdminLoginResult;
import com.moassam.admin.application.provided.AdminAuth;
import com.moassam.admin.exception.AdminAuthErrorCode;
import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.RestDocsSupport;
import com.moassam.shared.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAuthApiTest extends RestDocsSupport {

    private final AdminAuth adminAuth = mock(AdminAuth.class);
    private final HttpOnlyCookie adminAccessTokenCookie = mock(HttpOnlyCookie.class);
    private final HttpOnlyCookie adminRefreshTokenCookie = mock(HttpOnlyCookie.class);

    @Override
    protected Object initController() {
        return new AdminAuthApi(adminAuth, adminAccessTokenCookie, adminRefreshTokenCookie);
    }

    @BeforeEach
    void setUpAdminResolver(RestDocumentationContextProvider provider) {
        mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setControllerAdvice(new com.moassam.shared.web.ApiControllerAdvice())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .setCustomArgumentResolvers(new TestCurrentUserIdResolver())
                .apply(documentationConfiguration(provider))
                .build();
    }

    @Test
    void login() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest("super-admin", "password");
        given(adminAuth.login("super-admin", "password"))
                .willReturn(new AdminLoginResult("admin-access-token", "admin-refresh-token"));

        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("admin/login",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("관리자 아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음. 관리자 토큰은 HttpOnly 쿠키로 발급")
                        )
                ));

        verify(adminAccessTokenCookie).add(any(), org.mockito.ArgumentMatchers.eq("admin-access-token"));
        verify(adminRefreshTokenCookie).add(any(), org.mockito.ArgumentMatchers.eq("admin-refresh-token"));
    }

    @Test
    void refresh() throws Exception {
        given(adminAuth.refresh("admin-refresh-token")).willReturn("new-admin-access-token");

        mockMvc.perform(post("/api/v1/admin/auth/refresh")
                        .cookie(new Cookie("adminRefreshToken", "admin-refresh-token")))
                .andExpect(status().isOk())
                .andDo(document("admin/refresh",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음. 관리자 access token은 HttpOnly 쿠키로 재발급")
                        )
                ));

        verify(adminAccessTokenCookie).add(any(), org.mockito.ArgumentMatchers.eq("new-admin-access-token"));
    }

    @Test
    void refresh_failed_clearsCookies() throws Exception {
        given(adminAuth.refresh("invalid-admin-refresh-token"))
                .willThrow(new BusinessException(AdminAuthErrorCode.INVALID_TOKEN));

        mockMvc.perform(post("/api/v1/admin/auth/refresh")
                        .cookie(new Cookie("adminRefreshToken", "invalid-admin-refresh-token")))
                .andExpect(status().isUnauthorized());

        verify(adminAccessTokenCookie).clearAll(any());
        verify(adminRefreshTokenCookie).clearAll(any());
    }


    @Test
    void logout() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, List.of())
        );

        mockMvc.perform(post("/api/v1/admin/auth/logout"))
                .andExpect(status().isNoContent())
                .andDo(document("admin/logout",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));

        verify(adminAuth).logout(1L);
        verify(adminAccessTokenCookie).clearAll(any());
        verify(adminRefreshTokenCookie).clearAll(any());

        SecurityContextHolder.clearContext();
    }

    private static class TestCurrentUserIdResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().equals(Long.class);
        }

        @Override
        public Object resolveArgument(
                MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory
        ) {
            return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        }
    }
}