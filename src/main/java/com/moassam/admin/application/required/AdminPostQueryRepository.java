package com.moassam.admin.application.required;

import com.moassam.admin.application.dto.AdminPostSummary;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface AdminPostQueryRepository extends Repository<Post, Long> {
    @Query("""
                select new com.moassam.admin.application.dto.AdminPostSummary(
                p.id,
                p.title,
                u.nickname,
                p.category,
                p.createdAt,
                p.viewCount
                )
                from Post p
                join User u on u.id = p.userId
                where (:category is null or p.category = :category)
                    and (
                        :keyword is null
                        or lower(p.title) like lower(concat('%', :keyword, '%'))
                        or lower(u.nickname) like lower (concat('%', :keyword, '%')) 
                    )
                order by p.createdAt desc
            """)
    Page<AdminPostSummary> findAdminPostSummaries(
            Category category,
            String keyword,
            Pageable pageable
    );
}
