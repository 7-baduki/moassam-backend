package com.moassam.post.application.required;

import com.moassam.post.domain.post.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post save(Post post);

    @Query("""
        select p
        from Post p
        where p.category = :category
            and (:postAge is null or p.postAge = :postAge)
            and (:resourceType is null or p.resourceType = :resourceType)
        order by p.createdAt desc
    """)
    Page<Post> findMoabangDashboard(
            Category category,
            PostAge postAge,
            ResourceType resourceType,
            Pageable pageable
    );

    @Query("""
        select p
        from Post p
        where p.category = :category
            and (:headTag is null or p.headTag = :headTag)
        order by p.createdAt desc
    """)
    Page<Post> findFreeDashboard(
            Category category,
            HeadTag headTag,
            Pageable pageable
    );

    Page<Post> findAllByCategoryAndTitleContainingIgnoreCase(
            Category category,
            String keyword,
            Pageable pageable
    );

    Page<Post> findAllByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, Category category, Pageable pageable);

    @Modifying
    @Query("""
    update Post p
    set p.commentCount = (
        select count(c)
        from Comment c
        where c.postId = p.id
    )
    where p.id in :postIds
""")
    void recalculateCommentCounts(List<Long> postIds);

    @Modifying
    @Query("""
    update Post p
    set p.likeCount = (
        select count(l)
        from PostLike l
        where l.postId = p.id
    )
    where p.id in :postIds
""")
    void recalculateLikeCounts(List<Long> postIds);
}
