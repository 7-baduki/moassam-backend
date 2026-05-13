package com.moassam.user.application.provided;

import com.moassam.user.application.dto.*;
import org.springframework.data.domain.Page;

public interface UserActivity {

    Page<MyMoabangPostResponse> getMyMoabangPosts(Long userId, int page, int size);

    Page<MyFreePostResponse> getMyFreePosts(Long userId, int page, int size);

    Page<MyCommentResponse> getMyComments(Long userId, int page, int size);

    Page<MyObservationResponse> getMyObservations(Long userId, int page, int size);

    Page<MyBookmarkedResponse> getMyBookmarkedPosts(Long userId, int page, int size);

    MyActivityCountsResponse getMyActivityCounts(Long userId);
}