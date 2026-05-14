package com.moassam.post.application.required;

import com.moassam.post.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        select new com.moassam.post.application.required.MyCommentProjection(
            c.id,
            p.id,
            p.category,
            c.content,
            p.title,
            c.createdAt
        )
        from Comment c
        join Post p on p.id = c.postId
        where c.userId = :userId
        order by c.createdAt desc
    """)
    Page<MyCommentProjection> findMyCommentsByUserId(Long userId, Pageable pageable);

    @Query("""
        select c
        from Comment c
        join Post p on p.id = c.postId
        where c.postId = :postId
        order by c.createdAt asc
    """)
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}
