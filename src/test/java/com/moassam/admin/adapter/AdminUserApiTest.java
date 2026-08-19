package com.moassam.admin.adapter;

import com.moassam.admin.application.AdminUserService;
import com.moassam.admin.application.dto.AdminUserSearchResult;
import com.moassam.admin.application.dto.AdminUserSummary;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.user.domain.Provider;
import com.moassam.user.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminUserApiTest extends RestDocsSupport {

    private final AdminUserService adminUserService = org.mockito.Mockito.mock(AdminUserService.class);

    @Override
    protected Object initController() {
        return new AdminUserApi(adminUserService);
    }

    @Test
    void getUsers() throws Exception {
        var activeUser = new AdminUserSummary(
                2L,
                "활성 사용자",
                Provider.KAKAO,
                Role.TEACHER,
                LocalDateTime.of(2026, 6, 30, 23, 59),
                null
        );
        var withdrawnUser = new AdminUserSummary(
                1L,
                "탈퇴한 사용자",
                Provider.KAKAO,
                Role.TEACHER,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2026, 7, 1, 10, 0)
        );
        var users = new PageImpl<>(
                List.of(activeUser, withdrawnUser),
                PageRequest.of(0, 20),
                2
        );
        given(adminUserService.getUsers(
                eq(LocalDate.of(2026, 6, 1)),
                eq(LocalDate.of(2026, 6, 30)),
                eq(0),
                eq(20)
        )).willReturn(new AdminUserSearchResult(15L, users));

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-06-30")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalUserCount").value(15))
                .andExpect(jsonPath("$.data.periodUserCount").value(2))
                .andExpect(jsonPath("$.data.users.data[0].userId").value(2))
                .andExpect(jsonPath("$.data.users.data[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.users.data[1].status").value("WITHDRAWN"))
                .andDo(document("admin/user/get-users",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("startDate").description("가입 시작일(yyyy-MM-dd), 해당 날짜 포함").optional(),
                                parameterWithName("endDate").description("가입 종료일(yyyy-MM-dd), 해당 날짜 포함").optional(),
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 20, 최대 100").optional()
                        ),
                        responseFields(CommonDocumentation.successResponseFields(
                                fieldWithPath("data.totalUserCount").type(JsonFieldType.NUMBER).description("전체 누적 유저 수"),
                                fieldWithPath("data.periodUserCount").type(JsonFieldType.NUMBER).description("선택 기간 가입자 수"),
                                fieldWithPath("data.users.data").type(JsonFieldType.ARRAY).description("사용자 목록"),
                                fieldWithPath("data.users.data[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.users.data[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.users.data[].provider").type(JsonFieldType.STRING).description("가입 경로"),
                                fieldWithPath("data.users.data[].role").type(JsonFieldType.STRING).description("사용자 역할"),
                                fieldWithPath("data.users.data[].createdAt").type(JsonFieldType.STRING).description("가입 일시"),
                                fieldWithPath("data.users.data[].status").type(JsonFieldType.STRING).description("사용자 상태: ACTIVE, WITHDRAWN"),
                                fieldWithPath("data.users.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.users.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("data.users.totalElements").type(JsonFieldType.NUMBER).description("기간 내 전체 가입자 수"),
                                fieldWithPath("data.users.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.users.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                        ))
                ));
    }

    @Test
    void getUsers_invalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users").param("startDate", "2026/06/01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.properties.code").value("INVALID_REQUEST"));
    }

    @Test
    void getUsers_invalidRange() throws Exception {
        given(adminUserService.getUsers(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 6, 1),
                0,
                20
        )).willThrow(new IllegalArgumentException("invalid range"));

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-06-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.properties.code").value("INVALID_REQUEST"));
    }
}
