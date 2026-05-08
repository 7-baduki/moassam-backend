package com.moassam.user.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.CurriculumType;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.PostAge;
import com.moassam.post.domain.post.ResourceType;
import com.moassam.support.UserFixture;
import com.moassam.user.application.dto.MyCommentResponse;
import com.moassam.user.application.dto.MyFreePostResponse;
import com.moassam.user.application.dto.MyMoabangPostResponse;
import com.moassam.user.application.dto.MyObservationResponse;
import com.moassam.user.adapter.web.dto.MyPostResponse;
import com.moassam.user.application.provided.UserActivity;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiTest extends RestDocsSupport {

    private final UserProfile userProfile = mock(UserProfile.class);
    private final UserActivity userActivity = mock(UserActivity.class);

    @Override
    protected Object initController() {
        return new UserApi(userProfile, userActivity);
    }

    @Test
    void getProfile() throws Exception {
        User user = UserFixture.create();
        given(userProfile.getProfile(any())).willReturn(user);

        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk())
                .andDo(document("user/get-profile",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                                )
                        )
                ));
    }

    @Test
    void updateNickname() throws Exception {
        User user = UserFixture.createWithNickname("새닉네임");
        given(userProfile.updateNickname(any(), eq("새닉네임"))).willReturn(user);

        mockMvc.perform(patch("/api/v1/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"새닉네임\""))
                .andExpect(status().isOk())
                .andDo(document("user/update-nickname",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                                )
                        )
                ));
    }

    @Test
    void getMyMoabangPosts() throws Exception {
        MyMoabangPostResponse post = new MyMoabangPostResponse(1L, "5월 가정의달 수업 활동지 공유합니다", PostAge.AGE_5, ResourceType.JOURNAL, 56L, LocalDateTime.of(2026, 3, 6, 0, 0));

        given(userActivity.getMyMoabangPosts(any(), eq(0), eq(10)))
                .willReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users/posts/moabang")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("user/get-my-moabang-posts",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 10").optional()
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("모아방 게시글 목록"),
                                        fieldWithPath("data.data[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("data.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.data[].postAge").type(JsonFieldType.STRING).description("연령"),
                                        fieldWithPath("data.data[].resourceType").type(JsonFieldType.STRING).description("자료 유형"),
                                        fieldWithPath("data.data[].viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                        fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                ));
    }

    @Test
    void getMyFreePosts() throws Exception {
        MyFreePostResponse post = new MyFreePostResponse(1L, "선생님들 평균퇴근 시간 몇시인가요?", HeadTag.QUESTION, 56L, LocalDateTime.of(2026, 3, 6, 0, 0));

        given(userActivity.getMyFreePosts(any(), eq(0), eq(10)))
                .willReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users/posts/free")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("user/get-my-free-posts",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 10").optional()
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("자유게시판 게시글 목록"),
                                        fieldWithPath("data.data[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("data.data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.data[].headTag").type(JsonFieldType.STRING).description("말머리"),
                                        fieldWithPath("data.data[].viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                        fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                ));
    }

    @Test
    void getMyComments() throws Exception {
        MyCommentResponse comment = new MyCommentResponse(1L, "CD를 활용해 자동차를 표현하는 아이디어가 참신해요", "[활동자료] 나만의 자동차 그리기", LocalDateTime.of(2026, 3, 6, 0, 0));

        given(userActivity.getMyComments(any(), eq(0), eq(10)))
                .willReturn(new PageImpl<>(List.of(comment), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users/comments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("user/get-my-comments",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 10").optional()
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("댓글 목록"),
                                        fieldWithPath("data.data[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                        fieldWithPath("data.data[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.data[].postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 댓글 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                ));
    }

    @Test
    void getMyObservations() throws Exception {
        MyObservationResponse observation = new MyObservationResponse(1L, "자유놀이 시 특징 친구와의 상호작품", Age.AGE_4, CurriculumType.NURI, LocalDateTime.of(2026, 3, 6, 0, 0));

        given(userActivity.getMyObservations(any(), eq(0), eq(10)))
                .willReturn(new PageImpl<>(List.of(observation), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users/observations")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("user/get-my-observations",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호, 0부터 시작").optional(),
                                parameterWithName("size").description("페이지 크기, 기본값 10").optional()
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("관찰일지 목록"),
                                        fieldWithPath("data.data[].observationId").type(JsonFieldType.NUMBER).description("관찰일지 ID"),
                                        fieldWithPath("data.data[].title").type(JsonFieldType.STRING).description("관찰일지 제목"),
                                        fieldWithPath("data.data[].age").type(JsonFieldType.STRING).description("연령"),
                                        fieldWithPath("data.data[].curriculumType").type(JsonFieldType.STRING).description("교육과정: STANDARD(표준), NURI(누리)"),
                                        fieldWithPath("data.data[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 관찰일지 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                                )
                        )
                ));
    }
}