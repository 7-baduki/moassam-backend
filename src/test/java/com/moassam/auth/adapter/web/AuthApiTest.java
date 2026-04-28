package com.moassam.auth.adapter.web;

import com.moassam.auth.adapter.security.cookie.RefreshTokenCookie;
import com.moassam.auth.application.provided.Auth;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends RestDocsSupport {

    private final Auth auth = mock(Auth.class);
    private final RefreshTokenCookie refreshTokenCookie = mock(RefreshTokenCookie.class);

    @Override
    protected Object initController() {
        return new AuthApi(auth, refreshTokenCookie);
    }

    @Test
    void refresh() throws Exception {
        given(auth.refresh("test-refresh-token")).willReturn("new-access-token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refreshToken", "test-refresh-token")))
                .andExpect(status().isOk())
                .andDo(document("auth/refresh",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("새로 발급된 액세스 토큰")
                                )
                        )
                ));
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent())
                .andDo(document("auth/logout",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));
    }

    @Test
    void withdraw() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/withdraw"))
                .andExpect(status().isNoContent())
                .andDo(document("auth/withdraw",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));
    }
}