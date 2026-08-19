package com.moassam.admin.application.required;

import com.moassam.admin.application.dto.AdminUserSummary;
import com.moassam.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;

public interface AdminUserQueryRepository extends Repository<User, Long> {

    long count();

    @Query("""
            select new com.moassam.admin.application.dto.AdminUserSummary(
                u.id,
                u.nickname,
                u.provider,
                u.role,
                u.createdAt,
                u.deletedAt
            )
            from User u
            where (:startAt is null or u.createdAt >= :startAt)
              and (:endAt is null or u.createdAt < :endAt)
            order by u.createdAt desc, u.id desc
            """)
    Page<AdminUserSummary> findAdminUserSummaries(
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable
    );
}
