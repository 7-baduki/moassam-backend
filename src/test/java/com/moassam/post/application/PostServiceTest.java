package com.moassam.post.application;

import com.moassam.credit.application.provided.CreditCharger;
import com.moassam.post.application.dto.PostDetail;
import com.moassam.post.application.required.*;
import com.moassam.post.domain.comment.Comment;
import com.moassam.post.domain.post.*;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.adapter.filestorage.FileStorage;
import com.moassam.shared.exception.BusinessException;
import com.moassam.support.CommentFixture;
import com.moassam.support.UserFixture;
import com.moassam.support.post.PostFixture;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class PostServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostFileRepository postFileRepository = mock(PostFileRepository.class);
    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final BookmarkRepository bookmarkRepository = mock(BookmarkRepository.class);
    private final PostViewRepository postViewRepository = mock(PostViewRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final FileStorage fileStorage = mock(FileStorage.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CreditCharger creditCharger = mock(CreditCharger.class);

    private final PostService postService = new PostService(
            postRepository,
            postFileRepository,
            userRepository,
            postLikeRepository,
            bookmarkRepository,
            postViewRepository,
            fileStorage,
            creditCharger,
            commentRepository
    );

    @Test
    void createPost() {
        PostCreateRequest request = new PostCreateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "질문 있습니다.",
                "게시글 내용"
        );

        given(postRepository.save(any(Post.class)))
                .willAnswer(invocation -> {
                    Post post = invocation.getArgument(0);
                    org.springframework.test.util.ReflectionTestUtils.setField(post, "id", 1L);
                    return post;
                });

        given(fileStorage.upload(any(), eq("posts")))
                .willReturn("https://example.com/test.pdf");

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.pdf",
                "application/pdf",
                "file-content".getBytes()
        );

        Long postId = postService.createPost(1L, request, List.of(file), List.of());

        assertThat(postId).isEqualTo(1L);
        then(postRepository).should().save(any(Post.class));
        then(postFileRepository).should().saveAll(anyList());
        then(creditCharger).should().chargeForFreePost(1L, postId);
    }

    @Test
    void editorImage_Extension_jpg_jpeg_png() {
        PostCreateRequest request = new PostCreateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "제목",
                "내용"
        );

        given(postRepository.save(any(Post.class)))
                .willAnswer(invocation -> {
                    Post post = invocation.getArgument(0);
                    ReflectionTestUtils.setField(post, "id", 1L);
                    return post;
                });

        given(fileStorage.upload(any(), eq("posts")))
                .willReturn("https://example.com/image.png");

        MockMultipartFile png = new MockMultipartFile(
                "editorImages",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "image-content".getBytes()
        );



        Long postId = postService.createPost(1L, request, List.of(), List.of(png));

        assertThat(postId).isEqualTo(1L);
        then(postFileRepository).should().saveAll(anyList());
    }

    @Test
    void fail_EditorExtension() {
        PostCreateRequest request = new PostCreateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "제목",
                "내용"
        );

        MockMultipartFile invalidEditorImage = new MockMultipartFile(
                "editorImages",
                "not-image.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "content".getBytes()
        );

        assertThatThrownBy(() ->
                postService.createPost(1L, request, List.of(), List.of(invalidEditorImage))
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_INVALID_EDITOR_IMAGE);
    }

    @Test
    void fail_TotalFile_Exceed_10MB() {
        PostCreateRequest request = new PostCreateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "제목",
                "내용"
        );

        MockMultipartFile largeFile = new MockMultipartFile(
                "files",
                "large.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[10 * 1024 * 1024 + 1]
        );

        assertThatThrownBy(() ->
                postService.createPost(1L, request, List.of(largeFile), List.of())
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_UPLOAD_SIZE_EXCEEDED);
    }

    @Test
    void fail_File_EditorImage_Exceed_10MB() {
        PostCreateRequest request = new PostCreateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "제목",
                "내용"
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[6 * 1024 * 1024]
        );

        MockMultipartFile editorImage = new MockMultipartFile(
                "editorImages",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[5 * 1024 * 1024]
        );

        assertThatThrownBy(() ->
                postService.createPost(1L, request, List.of(file), List.of(editorImage))
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_UPLOAD_SIZE_EXCEEDED);
    }

    @Test
    void getPost_firstView_increaseViewCount() {
        Post post = PostFixture.createFreePost(10L);

        User user = UserFixture.createWithNickname("author");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postFileRepository.findAllByPostId(10L)).willReturn(List.of());
        given(postLikeRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(postViewRepository.insertIgnore(10L, 2L)).willReturn(1);

        PostDetail result = postService.getPost(2L, 10L);

        assertThat(result.post().getViewCount()).isEqualTo(1);

        then(postViewRepository).should().insertIgnore(10L, 2L);
    }

    @Test
    void getPost_alreadyViewed_doesNotIncreaseViewCount() {
        Post post = PostFixture.createFreePost(10L);
        post.increaseViewCount();

        User user = UserFixture.createWithNickname("author");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postFileRepository.findAllByPostId(10L)).willReturn(List.of());
        given(postLikeRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(postViewRepository.insertIgnore(10L, 2L)).willReturn(0);

        PostDetail result = postService.getPost(2L, 10L);

        assertThat(result.post().getViewCount()).isEqualTo(1);

        then(postViewRepository).should().insertIgnore(10L, 2L);
    }

    @Test
    void getPost_byAuthor_returnsIsMineTrue() {
        Post post = PostFixture.createFreePost(10L);

        User user = UserFixture.createWithNickname("author");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postFileRepository.findAllByPostId(10L)).willReturn(List.of());
        given(commentRepository.findAllByPostIdOrderByCreatedAtAsc(10L)).willReturn(List.of());
        given(postLikeRepository.existsByPostIdAndUserId(10L, 1L)).willReturn(false);
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 1L)).willReturn(false);
        given(postViewRepository.insertIgnore(10L, 1L)).willReturn(1);

        PostDetail result = postService.getPost(1L, 10L);

        assertThat(result.isMine()).isTrue();
    }

    @Test
    void getPost_byOtherUser_returnsIsMineFalse() {
        Post post = PostFixture.createFreePost(10L);

        User user = UserFixture.createWithNickname("author");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postFileRepository.findAllByPostId(10L)).willReturn(List.of());
        given(commentRepository.findAllByPostIdOrderByCreatedAtAsc(10L)).willReturn(List.of());
        given(postLikeRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(postViewRepository.insertIgnore(10L, 2L)).willReturn(1);

        PostDetail result = postService.getPost(2L, 10L);

        assertThat(result.isMine()).isFalse();
    }

    @Test
    void getPost_returnsCommentIsMineByCurrentUser() {
        Post post = PostFixture.createFreePost(10L);

        User user = UserFixture.createWithNickname("author");
        ReflectionTestUtils.setField(user, "id", 1L);

        Comment myComment = CommentFixture.createComment1(100L, 10L, 2L);
        Comment otherComment = CommentFixture.createComment2(101L, 10L, 3L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postFileRepository.findAllByPostId(10L)).willReturn(List.of());
        given(commentRepository.findAllByPostIdOrderByCreatedAtAsc(10L))
                .willReturn(List.of(myComment, otherComment));
        given(postLikeRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 2L)).willReturn(false);
        given(postViewRepository.insertIgnore(10L, 2L)).willReturn(1);

        PostDetail result = postService.getPost(2L, 10L);

        assertThat(result.comments()).hasSize(2);
        assertThat(result.comments().get(0).comment()).isEqualTo(myComment);
        assertThat(result.comments().get(0).isMine()).isTrue();
        assertThat(result.comments().get(1).comment()).isEqualTo(otherComment);
        assertThat(result.comments().get(1).isMine()).isFalse();
    }

    @Test
    void updatePost() {
        Post post = Post.create(
                1L,
                "기존 제목",
                "기존 내용",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(fileStorage.upload(any(), eq("posts")))
                .willReturn("https://example.com/new-file.pdf");

        PostUpdateRequest request = new PostUpdateRequest(
                Category.MOABANG,
                PostAge.AGE_3,
                ResourceType.JOURNAL,
                null,
                "수정 제목",
                "수정 내용",
                List.of()
        );

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "new-file.pdf",
                "application/pdf",
                "new-file-content".getBytes()
        );

        Long postId = postService.updatePost(1L, 1L, request, List.of(file), List.of());

        assertThat(postId).isEqualTo(post.getId());
        assertThat(post.getTitle()).isEqualTo("수정 제목");
        assertThat(post.getCategory()).isEqualTo(Category.MOABANG);
        then(postFileRepository).should().saveAll(anyList());
    }

    @Test
    void updatePost_notOwner() {
        Post post = Post.create(
                1L,
                "제목",
                "내용",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        PostUpdateRequest request = new PostUpdateRequest(
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION,
                "수정 제목",
                "수정 내용",
                List.of()
        );

        assertThatThrownBy(() ->
                postService.updatePost(2L, 1L, request, List.of(), List.of())
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_FORBIDDEN);
    }

    @Test
    void deletePost() {
        Post post = Post.create(
                1L,
                "제목",
                "내용",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        PostFile file = PostFile.create(
                1L,
                "test.pdf",
                "https://example.com/test.pdf",
                1024L,
                FileType.FILE
        );

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postFileRepository.findAllByPostId(1L)).willReturn(List.of(file));

        postService.deletePost(1L, 1L);

        then(fileStorage).should().delete("https://example.com/test.pdf");
        then(postRepository).should().delete(post);
        then(postFileRepository).should(never()).deleteAll(anyList());
    }

    @Test
    void deletePost_notFound() {
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }
}
