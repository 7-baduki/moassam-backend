package com.moassam.admin.adapter;

import com.moassam.admin.application.AdminPostService;
import com.moassam.admin.application.dto.AdminPostSummary;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.post.domain.post.Category;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminPostApiTest extends RestDocsSupport {

    private final AdminPostService adminPostService = mock(AdminPostService.class);

    @Override
    protected Object initController() {
        return new AdminPostApi(adminPostService);
    }

    @Test
    void getPosts() throws Exception {
        AdminPostSummary summary = new AdminPostSummary(
                1L, "게시글 제목", "작성자", Category.FREE,
                LocalDateTime.of(2026, 5, 8, 10, 30), 15L
        );

        given(adminPostService.getPosts(eq(Category.FREE), eq("게시글"), eq(0), eq(20)))
                .willReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/admin/posts")
                        .param("category", "FREE")
                        .param("keyword", "게시글")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data.data[0].title").value("게시글 제목"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andDo(document("admin/post/get-posts",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("category").description("게시글 카테고리: FREE, MOABANG").optional(),
                                parameterWithName("keyword").description("검색어").optional(),
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 20").optional()
                        ),
                        responseFields(CommonDocumentation.successResponseFields(
                                fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("관리자 게시글 목록"),
                                fieldWithPath("data.data[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("data.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("data.data[].author").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("data.data[].category").type(JsonFieldType.STRING).description("게시글 카테고리"),
                                fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                fieldWithPath("data.data[].viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                        ))
                ));
    }

    @Test
    void getPosts_allCategory() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 20);
        given(adminPostService.getPosts(isNull(), isNull(), eq(0), eq(20)))
                .willReturn(Page.empty(pageRequest));

        mockMvc.perform(get("/api/v1/admin/posts")
                        .param("category", "ALL")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(0));

        then(adminPostService).should().getPosts(null, null, 0, 20);
    }

    @Test
    void getPosts_invalidCategory() throws Exception {
        mockMvc.perform(get("/api/v1/admin/posts")
                        .param("category", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.properties.code").value("INVALID_REQUEST"));

        verifyNoInteractions(adminPostService);
    }

    @Test
    void deletePost() throws Exception {
        willDoNothing().given(adminPostService).deletePost(1L);

        mockMvc.perform(delete("/api/v1/admin/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("admin/post/delete-post",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        pathParameters(parameterWithName("postId").description("게시글 ID"))
                ));
    }
}
