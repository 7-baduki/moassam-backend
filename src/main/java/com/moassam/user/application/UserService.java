package com.moassam.user.application;

import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.post.application.required.CommentRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.Post;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.adapter.web.dto.MyCommentResponse;
import com.moassam.user.adapter.web.dto.MyObservationResponse;
import com.moassam.user.adapter.web.dto.MyPostResponse;
import com.moassam.user.application.provided.UserActivity;
import com.moassam.user.application.provided.UserProfile;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import com.moassam.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService implements UserProfile, UserActivity {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ObservationRepository observationRepository;

    @Override
    @Transactional
    public User updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateNickname(nickname);

        return user;
    }

    @Override
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Page<MyPostResponse> getMyPosts(Long userId, Category category, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        return postRepository.findAllByUserIdAndCategoryOrderByCreatedAtDesc(userId, category, pageable)
                .map(MyPostResponse::from);
    }

    @Override
    public Page<MyCommentResponse> getMyComments(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        return commentRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(comment -> {
                    Post post = postRepository.findById(comment.getPostId())
                            .orElse(null);
                    String postTitle = post != null ? post.getTitle() : "(삭제된 게시글)";
                    return new MyCommentResponse(
                            comment.getId(),
                            comment.getContent(),
                            postTitle,
                            comment.getCreatedAt()
                    );
                });
    }

    @Override
    public Page<MyObservationResponse> getMyObservations(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        return observationRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(MyObservationResponse::from);
    }
}