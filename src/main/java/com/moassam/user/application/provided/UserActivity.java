package com.moassam.user.application.provided;

import com.moassam.user.application.dto.MyCommentResponse;
import com.moassam.user.application.dto.MyFreePostResponse;
import com.moassam.user.application.dto.MyMoabangPostResponse;
import com.moassam.user.application.dto.MyObservationResponse;
import org.springframework.data.domain.Page;

public interface UserActivity {

    Page<MyMoabangPostResponse> getMyMoabangPosts(Long userId, int page, int size);

    Page<MyFreePostResponse> getMyFreePosts(Long userId, int page, int size);

    Page<MyCommentResponse> getMyComments(Long userId, int page, int size);

    Page<MyObservationResponse> getMyObservations(Long userId, int page, int size);
}