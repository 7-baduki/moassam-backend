package com.moassam.post.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.post.application.provided.dashboard.DashboardFinder;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.PostAge;
import com.moassam.post.domain.post.ResourceType;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardApiTest extends RestDocsSupport {

    private final DashboardFinder dashboardFinder = mock(DashboardFinder.class);

    @Override
    protected Object initController() {
        return new DashboardApi(dashboardFinder);
    }

    @Test
    void getMoabangDashboard() throws Exception {
        MoabangDashboardDetail detail = new MoabangDashboardDetail(
                1L,
                "5월 가정의달 수업 활동지 공유합니다(수정가능)",
                "햇살선생님",
                "https://example.com/default.png",
                PostAge.AGE_5,
                ResourceType.JOURNAL,
                1230L,
                46L,
                119L,
                LocalDateTime.of(2026, 5, 8, 10, 30)
        );

        given(dashboardFinder.getMoabangDashboard(
                any(),
                eq(PostAge.AGE_5),
                eq(ResourceType.JOURNAL),
                eq(0),
                eq(9)
        )).willReturn(new PageImpl<>(List.of(detail), PageRequest.of(0, 9), 1));

        mockMvc.perform(get("/api/v1/posts/moabang")
                        .param("postAge", "AGE_5")
                        .param("resourceType", "JOURNAL")
                        .param("page", "0")
                        .param("size", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data.data[0].title").value("5월 가정의달 수업 활동지 공유합니다(수정가능)"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(9))
                .andDo(document("dashboard/get-moabang",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("postAge").description("연령 필터: ALL, INFANT, AGE_3, AGE_4, AGE_5").optional(),
                                parameterWithName("resourceType").description("자료 유형 필터: ACTIVITY, PLAN, JOURNAL, NOTICE").optional(),
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 9").optional()
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("모아방 대시보드 게시글 목록"),
                                        fieldWithPath("data.data[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("data.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.data[].authorNickName").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                        fieldWithPath("data.data[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.data[].postAge").type(JsonFieldType.STRING).description("연령"),
                                        fieldWithPath("data.data[].resourceType").type(JsonFieldType.STRING).description("자료 유형"),
                                        fieldWithPath("data.data[].viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                        fieldWithPath("data.data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                        fieldWithPath("data.data[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호, 0부터 시작"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                ));
    }

    @Test
    void getFreeDashboard() throws Exception {
        FreeDashboardDetail detail = new FreeDashboardDetail(
                1L,
                "안녕하세요",
                "나미리선생님",
                "본문 미리보기",
                HeadTag.QUESTION,
                100L,
                10L,
                3L,
                LocalDateTime.of(2026, 5, 8, 10, 30)
        );

        given(dashboardFinder.getFreeDashboard(
                any(),
                eq(HeadTag.QUESTION),
                eq(0),
                eq(9)
        )).willReturn(new PageImpl<>(List.of(detail), PageRequest.of(0, 9), 1));

        mockMvc.perform(get("/api/v1/posts/free")
                        .param("headTag", "QUESTION")
                        .param("page", "0")
                        .param("size", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data.data[0].title").value("안녕하세요"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(9))
                .andDo(document("dashboard/get-free",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("headTag").description("말머리 필터: WORRY, QUESTION, CHAT").optional(),
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 9").optional()
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("자유게시판 대시보드 게시글 목록"),
                                        fieldWithPath("data.data[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("data.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.data[].authorNickName").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                        fieldWithPath("data.data[].contentPreview").type(JsonFieldType.STRING).description("본문 미리보기"),
                                        fieldWithPath("data.data[].headTag").type(JsonFieldType.STRING).description("말머리"),
                                        fieldWithPath("data.data[].viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                        fieldWithPath("data.data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                        fieldWithPath("data.data[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호, 0부터 시작"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                ));
    }
}
