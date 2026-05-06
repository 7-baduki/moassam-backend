package com.moassam.post.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.RestDocsSupport;
import com.moassam.post.application.provided.postlike.PostLikeRegister;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostLikeApiTest extends RestDocsSupport {

    private final PostLikeRegister postLikeRegister = mock(PostLikeRegister.class);

    @Override
    protected Object initController() {
        return new PostLikeApi(postLikeRegister);
    }

    @Test
    void like() throws Exception {
        mockMvc.perform(post("/api/v1/posts/{postId}/likes", 1L))
                .andExpect(status().isOk())
                .andDo(document("like/post-like",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));

        then(postLikeRegister).should().like(any(), eq(1L));
    }

    @Test
    void unlike() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/{postId}/likes", 1L))
                .andExpect(status().isOk())
                .andDo(document("like/delete-like",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));

        then(postLikeRegister).should().unlike(any(), eq(1L));
    }
}