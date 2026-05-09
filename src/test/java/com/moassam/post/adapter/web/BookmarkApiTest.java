package com.moassam.post.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.RestDocsSupport;
import com.moassam.post.application.provided.bookmark.BookmarkRegister;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkApiTest extends RestDocsSupport {

    private final BookmarkRegister bookmarkRegister = mock(BookmarkRegister.class);

    @Override
    protected Object initController() {
        return new BookmarkApi(bookmarkRegister);
    }

    @Test
    void bookmark() throws Exception {
        mockMvc.perform(post("/api/v1/posts/{postId}/bookmarks", 1L))
                .andExpect(status().isOk())
                .andDo(document("bookmark/post-bookmark",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));

        then(bookmarkRegister).should().bookmark(any(), eq(1L));
    }

    @Test
    void unbookmark() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/{postId}/bookmarks", 1L))
                .andExpect(status().isOk())
                .andDo(document("bookmark/delete-bookmark",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));

        then(bookmarkRegister).should().unbookmark(any(), eq(1L));
    }
}