package com.moassam.post.domain.post;

import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.domain.BaseEntity;
import com.moassam.shared.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Category category;
    private Age age;
    private ResourceType resourceType;
    private HeadTag headTag;

    private long viewCount;
    private long likeCount;
    private long commentCount;

    public static Post create(
            Long userId,
            String title,
            String content,
            Category category,
            Age age,
            ResourceType resourceType,
            HeadTag headTag
    ) {
        validateCategoryOptions(category, age, resourceType, headTag);


        Post post = new Post();

        post.userId = userId;
        post.title = title;
        post.content = content;
        post.category = category;
        post.age = age;
        post.resourceType = resourceType;
        post.headTag = headTag;

        return post;
    }

    public void update(
            String title,
            String content,
            Category category,
            Age age,
            ResourceType resourceType,
            HeadTag headTag
    ) {
        validateCategoryOptions(category, age, resourceType, headTag);

        this.title = title;
        this.content = content;
        this.category = category;
        this.age = age;
        this.resourceType = resourceType;
        this.headTag = headTag;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    private static void validateCategoryOptions(
            Category category,
            Age age,
            ResourceType resourceType,
            HeadTag headTag
    ) {
        if (category == Category.MOABANG) {
            if(age == null || resourceType == null) {
                throw new BusinessException(PostErrorCode.POST_INVALID_CATEGORY_OPTION, "모아방은 연령과 자료 유형이 필수입니다.");
            }
            if (headTag != null) {
                throw new BusinessException(PostErrorCode.POST_INVALID_CATEGORY_OPTION, "모아방은 말머리를 사용할 수 없습니다.");
            }
        }

        if (category == Category.FREE) {
            if (headTag == null) {
                throw new BusinessException(PostErrorCode.POST_INVALID_CATEGORY_OPTION, "자유게시판은 말머리가 필수입니다.");
            }
            if (age != null || resourceType != null) {
                throw new BusinessException(PostErrorCode.POST_INVALID_CATEGORY_OPTION, "자유게시판은 연령과 자료 유형을 사용할 수 없습니다.");
            }
        }
    }
}
