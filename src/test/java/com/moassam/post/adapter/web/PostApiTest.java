package com.moassam.post.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.post.application.provided.post.PostCreator;
import com.moassam.post.application.provided.post.PostDeleter;
import com.moassam.post.application.provided.post.PostFinder;
import com.moassam.post.application.provided.post.PostUpdater;
import com.moassam.post.domain.post.*;
import com.moassam.support.post.PostFileFixture;
import com.moassam.support.post.PostFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostApiTest extends RestDocsSupport {

    private final PostCreator postCreator = mock(PostCreator.class);
    private final PostFinder postFinder = mock(PostFinder.class);
    private final PostUpdater postUpdater = mock(PostUpdater.class);
    private final PostDeleter postDeleter = mock(PostDeleter.class);

    @Override
    protected Object initController() {
        return new PostApi(postCreator, postFinder, postUpdater, postDeleter);
    }

    @Test
    void createFreePost() throws Exception {
        given(postCreator.createPost(anyLong(), any(), any(), any()))
                .willReturn(1L);

        PostCreateRequest freePost = new PostCreateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.WORRY,
                "이직 준비중인데 고민이 있습니다.",
                "게시글 내용"
        );

        MockMultipartFile freeRequestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(freePost)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "file-content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/posts")
                        .file(freeRequestPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("post/create-free",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestParts(
                                partWithName("request").description("게시글 등록 요청 데이터"),
                                partWithName("files").description("일반 첨부파일").optional(),
                                partWithName("editorImages").description("에디터 이미지 파일").optional()
                        ),
                        requestPartFields("request",
                                fieldWithPath("category").type(JsonFieldType.STRING).description("게시판 카테고리: FREE"),
                                fieldWithPath("age").type(JsonFieldType.NULL).description("자유게시판에서는 사용 X"),
                                fieldWithPath("resourceType").type(JsonFieldType.NULL).description("자유게시판에서는 사용 X"),
                                fieldWithPath("headTag").type(JsonFieldType.STRING).description("말머리: WORRY, QUESTION, CHAT"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 ID")
                                )
                        )));
    }

    @Test
    void createMoabangPost() throws Exception {
        given(postCreator.createPost(anyLong(), any(), any(), any()))
                .willReturn(1L);

        PostCreateRequest moaPost = new PostCreateRequest(
                Category.MOABANG,
                Age.AGE_3,
                ResourceType.ACTIVITY,
                null,
                "활동자료 어쩌구",
                "게시글 내용"
        );

        MockMultipartFile moaRequestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(moaPost)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "file-content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/posts")
                        .file(moaRequestPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("post/create-moabang",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestParts(
                                partWithName("request").description("게시글 등록 요청 데이터"),
                                partWithName("files").description("일반 첨부파일").optional(),
                                partWithName("editorImages").description("에디터 이미지 파일").optional()
                        ),
                        requestPartFields("request",
                                fieldWithPath("category").type(JsonFieldType.STRING).description("게시판 카테고리: MOABANG"),
                                fieldWithPath("age").type(JsonFieldType.STRING).description("연령: ALL, INFANT, AGE_3, AGE_4, AGE_5"),
                                fieldWithPath("resourceType").type(JsonFieldType.STRING).description("자료 유형: ACTIVITY, PLAN, JOURNAL, NOTICE"),
                                fieldWithPath("headTag").type(JsonFieldType.NULL).description("모아방에서는 사용 X"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 ID")
                                )
                        )));
    }

    @Test
    void getPost() throws Exception {
        Post freePost = PostFixture.createFreePost(1L);
        PostFile file = PostFileFixture.createFile(10L, 1L);
        PostFile editorImage = PostFileFixture.createEditorImage(11L, 1L);

        given(postFinder.getPost(any(), eq(1L)))
                .willReturn(new PostDetail(freePost, "햇살선생님",List.of(file, editorImage), false));

        mockMvc.perform(get("/api/v1/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("post/get-post",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("data.authorId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                        fieldWithPath("data.authorNickName").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("게시글 카테고리"),
                                        fieldWithPath("data.age").type(JsonFieldType.NULL).description("연령"),
                                        fieldWithPath("data.resourceType").type(JsonFieldType.NULL).description("자료 유형"),
                                        fieldWithPath("data.headTag").type(JsonFieldType.STRING).description("말머리"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),

                                        fieldWithPath("data.files").type(JsonFieldType.ARRAY).description("일반 첨부파일 목록"),
                                        fieldWithPath("data.files[].fileId").type(JsonFieldType.NUMBER).description("파일 ID"),
                                        fieldWithPath("data.files[].originalName").type(JsonFieldType.STRING).description("원본 파일명"),
                                        fieldWithPath("data.files[].url").type(JsonFieldType.STRING).description("파일 URL"),
                                        fieldWithPath("data.files[].size").type(JsonFieldType.NUMBER).description("파일 크기"),
                                        fieldWithPath("data.files[].fileType").type(JsonFieldType.STRING).description("파일 유형"),

                                        fieldWithPath("data.editorFiles").type(JsonFieldType.ARRAY).description("에디터 이미지 목록"),
                                        fieldWithPath("data.editorFiles[].fileId").type(JsonFieldType.NUMBER).description("에디터 이미지 ID"),
                                        fieldWithPath("data.editorFiles[].originalName").type(JsonFieldType.STRING).description("원본 파일명"),
                                        fieldWithPath("data.editorFiles[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                                        fieldWithPath("data.editorFiles[].size").type(JsonFieldType.NUMBER).description("이미지 크기"),
                                        fieldWithPath("data.editorFiles[].fileType").type(JsonFieldType.STRING).description("파일 유형"),

                                        fieldWithPath("data.viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                        fieldWithPath("data.commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                        fieldWithPath("data.bookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간")
                                )
                        )
                ));

    }

    @Test
    void updateFreePost() throws Exception {
        given(postUpdater.updatePost(anyLong(), eq(1L), any(), any(), any()))
                .willReturn(1L);

        PostUpdateRequest freePost = new PostUpdateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "수정된 제목",
                "수정된 내용",
                List.of(10L)
        );

        MockMultipartFile freeRequestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(freePost)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "new-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "new-file-content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/posts/{postId}", 1L)
                        .file(freeRequestPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(requestBuilder -> {
                            requestBuilder.setMethod("PATCH");
                            return requestBuilder;
                        }))
                .andExpect(status().isOk())
                .andDo(document("post/update-free",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestParts(
                                partWithName("request").description("게시글 수정 요청 데이터"),
                                partWithName("files").description("새로 추가할 일반 첨부파일").optional(),
                                partWithName("editorImages").description("새로 추가할 에디터 이미지 파일").optional()
                        ),
                        requestPartFields("request",
                                fieldWithPath("category").type(JsonFieldType.STRING).description("게시판 카테고리: FREE"),
                                fieldWithPath("age").type(JsonFieldType.NULL).description("자유게시판에서는 사용 X"),
                                fieldWithPath("resourceType").type(JsonFieldType.NULL).description("자유게시판에서는 사용 X"),
                                fieldWithPath("headTag").type(JsonFieldType.STRING).description("말머리: WORRY, QUESTION, CHAT"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("deleteFileIds").type(JsonFieldType.ARRAY).description("삭제할 기존 파일 ID 목록")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 ID")
                                )
                        )
                ));
    }

    @Test
    void updateMoabangPost() throws Exception {
        given(postUpdater.updatePost(anyLong(), eq(1L), any(), any(), any()))
                .willReturn(1L);

        PostUpdateRequest moaPost = new PostUpdateRequest(
                Category.MOABANG,
                Age.AGE_3,
                ResourceType.JOURNAL,
                null,
                "수정된 제목",
                "수정된 내용",
                List.of(10L)
        );

        MockMultipartFile moaRequestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(moaPost)
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "new-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "new-file-content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/posts/{postId}", 1L)
                        .file(moaRequestPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(requestBuilder -> {
                            requestBuilder.setMethod("PATCH");
                            return requestBuilder;
                        }))
                .andExpect(status().isOk())
                .andDo(document("post/update-moabang",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestParts(
                                partWithName("request").description("게시글 수정 요청 데이터"),
                                partWithName("files").description("새로 추가할 일반 첨부파일").optional(),
                                partWithName("editorImages").description("새로 추가할 에디터 이미지 파일").optional()
                        ),
                        requestPartFields("request",
                                fieldWithPath("category").type(JsonFieldType.STRING).description("게시판 카테고리: MOABANG"),
                                fieldWithPath("age").type(JsonFieldType.STRING).description("연령: ALL, INFANT, AGE_3, AGE_4, AGE_5"),
                                fieldWithPath("resourceType").type(JsonFieldType.STRING).description("자료 유형: ACTIVITY, PLAN, JOURNAL, NOTICE"),
                                fieldWithPath("headTag").type(JsonFieldType.NULL).description("모아방에서는 사용 X"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("deleteFileIds").type(JsonFieldType.ARRAY).description("삭제할 기존 파일 ID 목록")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 ID")
                                )
                        )
                ));
    }

    @Test
    void deletePost() throws Exception {
        willDoNothing().given(postDeleter).deletePost(anyLong(), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("post/delete-post",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));
    }

}
