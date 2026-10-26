package com.moassam.user.application.required;

import com.moassam.user.adapter.persistence.JdbcUserActivityRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(JdbcUserActivityRepository.class)
class UserActivityRepositoryTest {

    @Autowired
    private UserActivityRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void createAnalyticsTable() {
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS user_daily_activities (
                    activity_date DATE NOT NULL,
                    user_id BIGINT NOT NULL,
                    first_active_at TIMESTAMP NOT NULL,
                    last_active_at TIMESTAMP NOT NULL,
                    used_observation BOOLEAN NOT NULL DEFAULT FALSE,
                    used_community BOOLEAN NOT NULL DEFAULT FALSE,
                    observation_created_count BIGINT NOT NULL DEFAULT 0,
                    observation_regenerated_count BIGINT NOT NULL DEFAULT 0,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (activity_date, user_id)
                )
                """).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM user_daily_activities").executeUpdate();
    }

    @Test
    void upsert_mergesOneUsersDailyActivityWithoutDowngradingFlags() {
        LocalDate date = LocalDate.of(2026, 10, 25);
        repository.recordActive(date, 10L, LocalDateTime.of(2026, 10, 25, 9, 0));
        repository.recordObservationCreated(date, 10L, LocalDateTime.of(2026, 10, 25, 10, 0));
        repository.recordCommunityUsed(date, 10L, LocalDateTime.of(2026, 10, 25, 11, 0));
        repository.recordObservationRegenerated(date, 10L, LocalDateTime.of(2026, 10, 25, 12, 0));
        entityManager.flush();
        entityManager.clear();

        Object[] row = (Object[]) entityManager.createNativeQuery("""
                        SELECT first_active_at, last_active_at, used_observation, used_community,
                               observation_created_count, observation_regenerated_count
                        FROM user_daily_activities
                        WHERE activity_date = :date AND user_id = :userId
                        """)
                .setParameter("date", date)
                .setParameter("userId", 10L)
                .getSingleResult();

        assertThat(((Timestamp) row[0]).toLocalDateTime())
                .isEqualTo(LocalDateTime.of(2026, 10, 25, 9, 0));
        assertThat(((Timestamp) row[1]).toLocalDateTime())
                .isEqualTo(LocalDateTime.of(2026, 10, 25, 12, 0));
        assertThat(row[2]).isEqualTo(true);
        assertThat(row[3]).isEqualTo(true);
        assertThat(((Number) row[4]).longValue()).isEqualTo(1L);
        assertThat(((Number) row[5]).longValue()).isEqualTo(1L);
    }
}
