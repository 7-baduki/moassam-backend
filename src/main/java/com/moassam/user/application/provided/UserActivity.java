package com.moassam.user.application.provided;

import com.moassam.post.domain.post.Category;
import com.moassam.user.adapter.web.dto.MyCommentResponse;
import com.moassam.user.adapter.web.dto.MyObservationResponse;
import com.moassam.user.adapter.web.dto.MyPostResponse;
import org.springframework.data.domain.Page;

public interface UserActivity {

    Page<MyPostResponse> getMyPosts(Long userId, Category category, int page, int size);

    Page<MyCommentResponse> getMyComments(Long userId, int page, int size);

    Page<MyObservationResponse> getMyObservations(Long userId, int page, int size);
}