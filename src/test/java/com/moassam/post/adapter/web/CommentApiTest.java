package com.moassam.post.adapter.web;


import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.post.application.dto.CommentDetail;
import com.moassam.post.application.provided.comment.*;
import com.moassam.post.domain.comment.Comment;
import com.moassam.post.domain.comment.CommentCreateRequest;
import com.moassam.post.domain.comment.CommentUpdateRequest;
import com.moassam.support.CommentFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentApiTest extends RestDocsSupport {

    private final CommentCreator commentCreator = mock(CommentCreator.class);
    private final CommentFinder commentFinder = mock(CommentFinder.class);
    private final CommentUpdater commentUpdater = mock(CommentUpdater.class);
    private final CommentDeleter commentDeleter = mock(CommentDeleter.class);

    @Override
    protected Object initController() {
        return new CommentApi(commentCreator, commentFinder, commentUpdater, commentDeleter);
    }

    @Test
    void createComment() throws Exception {
        given(commentCreator.createComment(anyLong(), eq(1L), anyString())).willReturn(10L);

        CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("comment/create-comment",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                                )
                        )
                ));
    }

    @Test
    void getComment() throws Exception {
        Comment comment = CommentFixture.createComment1(10L, 1L, 1L);

        CommentDetail commentDetail = new CommentDetail(comment, true);
        given(commentFinder.getComment(any(), eq(1L), eq(10L))).willReturn(commentDetail);

        mockMvc.perform(get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 10L))
                .andExpect(status().isOk())
                .andDo(document("comment/get-comment",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("data.authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.isMine").type(JsonFieldType.BOOLEAN).description("댓글 작성자 여부"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간")
                                )
                        )
                ));
    }

    @Test
    void updateComment() throws Exception {
        given(commentUpdater.updateComment(anyLong(), eq(1L), eq(10L), anyString())).willReturn(10L);

        CommentUpdateRequest request = new CommentUpdateRequest("수정 댓글");

        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("comment/update-comment",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("수정 댓글 내용")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                                )
                        )
                ));
    }

    @Test
    void deleteComment() throws Exception {
        willDoNothing().given(commentDeleter).deleteComment(anyLong(), eq(1L), eq(10L));

        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", 1L, 10L))
                .andExpect(status().isOk())
                .andDo(document("comment/delete-comment",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));
    }
}
