package com.moassam.admin.application.required;

import com.moassam.shared.config.JpaAuditingConfig;
import com.moassam.user.domain.Provider;
import com.moassam.user.domain.User;
import com.moassam.user.domain.UserRegisterRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
class AdminUserQueryRepositoryTest {

    @Autowired
    private AdminUserQueryRepository adminUserQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAdminUserSummaries_withoutDateFilters() {
        User earlier = persistUser("provider-1", "이전 사용자");
        User later = persistUser("provider-2", "최근 사용자");
        setCreatedAt(earlier.getId(), LocalDateTime.of(2026, 6, 1, 0, 0));
        setCreatedAt(later.getId(), LocalDateTime.of(2026, 6, 30, 23, 59));
        entityManager.clear();

        var result = adminUserQueryRepository.findAdminUserSummaries(
                null,
                null,
                PageRequest.of(0, 20)
        );

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(user -> user.userId())
                .containsExactly(later.getId(), earlier.getId());
        assertThat(adminUserQueryRepository.count()).isEqualTo(2);
    }

    @Test
    void findAdminUserSummaries_includesBothDateBoundaries() {
        User atStart = persistUser("provider-start", "시작 경계");
        User atEnd = persistUser("provider-end", "종료 경계");
        User outside = persistUser("provider-outside", "범위 밖");
        setCreatedAt(atStart.getId(), LocalDateTime.of(2026, 6, 1, 0, 0));
        setCreatedAt(atEnd.getId(), LocalDateTime.of(2026, 6, 30, 23, 59, 59));
        setCreatedAt(outside.getId(), LocalDateTime.of(2026, 7, 1, 0, 0));
        entityManager.clear();

        var result = adminUserQueryRepository.findAdminUserSummaries(
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0),
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent())
                .extracting(user -> user.userId())
                .containsExactly(atEnd.getId(), atStart.getId());
    }

    @Test
    void findAdminUserSummaries_supportsOpenEndedRangeAndWithdrawnUser() {
        User before = persistUser("provider-before", "이전 사용자");
        User withdrawn = persistUser("provider-withdrawn", "탈퇴 대상");
        withdrawn.withdraw();
        entityManager.flush();
        setCreatedAt(before.getId(), LocalDateTime.of(2026, 5, 31, 23, 59));
        setCreatedAt(withdrawn.getId(), LocalDateTime.of(2026, 6, 1, 0, 0));
        entityManager.clear();

        var result = adminUserQueryRepository.findAdminUserSummaries(
                LocalDateTime.of(2026, 6, 1, 0, 0),
                null,
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).userId()).isEqualTo(withdrawn.getId());
        assertThat(result.getContent().get(0).deletedAt()).isNotNull();
    }

    @Test
    void findAdminUserSummaries_ordersSameTimestampByIdDescending() {
        User first = persistUser("provider-first", "첫 사용자");
        User second = persistUser("provider-second", "두 번째 사용자");
        LocalDateTime sameTime = LocalDateTime.of(2026, 6, 10, 12, 0);
        setCreatedAt(first.getId(), sameTime);
        setCreatedAt(second.getId(), sameTime);
        entityManager.clear();

        var result = adminUserQueryRepository.findAdminUserSummaries(
                null,
                LocalDateTime.of(2026, 7, 1, 0, 0),
                PageRequest.of(0, 20)
        );

        assertThat(result.getContent())
                .extracting(user -> user.userId())
                .containsExactly(second.getId(), first.getId());
    }

    private User persistUser(String providerId, String nickname) {
        User user = User.register(new UserRegisterRequest(
                Provider.KAKAO,
                providerId,
                providerId + "@example.com",
                nickname,
                null
        ));
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private void setCreatedAt(Long userId, LocalDateTime createdAt) {
        entityManager.createNativeQuery("UPDATE users SET created_at = :createdAt WHERE id = :userId")
                .setParameter("createdAt", createdAt)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
