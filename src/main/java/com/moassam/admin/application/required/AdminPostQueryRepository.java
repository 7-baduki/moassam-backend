package com.moassam.admin.application.required;

import com.moassam.admin.application.dto.AdminPostSummary;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface AdminPostQueryRepository extends Repository<Post, Long> {

    default Page<AdminPostSummary> findAdminPostSummaries(
            Category category,
            String keyword,
            Pageable pageable
    ) {
        if (category == null && keyword == null) {
            return findAllAdminPostSummaries(pageable);
        }
        if (category == null) {
            return findAdminPostSummariesByKeyword(keyword, pageable);
        }
        if (keyword == null) {
            return findAdminPostSummariesByCategory(category, pageable);
        }
        return findAdminPostSummariesByCategoryAndKeyword(category, keyword, pageable);
    }

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
                order by p.createdAt desc
            """)
    Page<AdminPostSummary> findAllAdminPostSummaries(Pageable pageable);

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
                where p.category = :category
                order by p.createdAt desc
            """)
    Page<AdminPostSummary> findAdminPostSummariesByCategory(
            Category category,
            Pageable pageable
    );

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
                where lower(p.title) like lower(concat('%', :keyword, '%'))
                    or lower(u.nickname) like lower(concat('%', :keyword, '%'))
                order by p.createdAt desc
            """)
    Page<AdminPostSummary> findAdminPostSummariesByKeyword(
            String keyword,
            Pageable pageable
    );

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
                where p.category = :category
                    and (
                        lower(p.title) like lower(concat('%', :keyword, '%'))
                        or lower(u.nickname) like lower(concat('%', :keyword, '%'))
                    )
                order by p.createdAt desc
            """)
    Page<AdminPostSummary> findAdminPostSummariesByCategoryAndKeyword(
            Category category,
            String keyword,
            Pageable pageable
    );
}
