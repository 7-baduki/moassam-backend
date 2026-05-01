package com.moassam.user.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.support.UserFixture;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiTest extends RestDocsSupport {

    private final UserProfile userProfile = mock(UserProfile.class);

    @Override
    protected Object initController() {
        return new UserApi(userProfile);
    }

    @Test
    void updateNickname() throws Exception {
        User user = UserFixture.createWithNickname("새닉네임");
        given(userProfile.updateNickname(1L, "새닉네임")).willReturn(user);

        mockMvc.perform(patch("/api/v1/users/me/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\": \"새닉네임\"}"))
                .andExpect(status().isOk())
                .andDo(document("user/update-nickname",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                                )
                        )
                ));
    }
}