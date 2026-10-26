package com.moassam.user.adapter.persistence;

import com.moassam.user.application.required.UserActivityRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public class JdbcUserActivityRepository implements UserActivityRepository {

    private static final String POSTGRES_UPSERT = """
            INSERT INTO user_daily_activities(
                activity_date, user_id, first_active_at, last_active_at,
                used_observation, used_community,
                observation_created_count, observation_regenerated_count,
                created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, now(), now())
            ON CONFLICT (activity_date, user_id) DO UPDATE SET
                first_active_at = LEAST(user_daily_activities.first_active_at, EXCLUDED.first_active_at),
                last_active_at = GREATEST(user_daily_activities.last_active_at, EXCLUDED.last_active_at),
                used_observation = user_daily_activities.used_observation OR EXCLUDED.used_observation,
                used_community = user_daily_activities.used_community OR EXCLUDED.used_community,
                observation_created_count = user_daily_activities.observation_created_count
                    + EXCLUDED.observation_created_count,
                observation_regenerated_count = user_daily_activities.observation_regenerated_count
                    + EXCLUDED.observation_regenerated_count,
                updated_at = now()
            """;

    private static final String H2_MERGE = """
            MERGE INTO user_daily_activities target
            USING (VALUES (?, ?, ?, ?, ?, ?, ?, ?)) source(
                activity_date, user_id, first_active_at, last_active_at,
                used_observation, used_community,
                observation_created_count, observation_regenerated_count
            )
            ON target.activity_date = source.activity_date AND target.user_id = source.user_id
            WHEN MATCHED THEN UPDATE SET
                first_active_at = LEAST(target.first_active_at, source.first_active_at),
                last_active_at = GREATEST(target.last_active_at, source.last_active_at),
                used_observation = target.used_observation OR source.used_observation,
                used_community = target.used_community OR source.used_community,
                observation_created_count = target.observation_created_count
                    + source.observation_created_count,
                observation_regenerated_count = target.observation_regenerated_count
                    + source.observation_regenerated_count,
                updated_at = CURRENT_TIMESTAMP
            WHEN NOT MATCHED THEN INSERT (
                activity_date, user_id, first_active_at, last_active_at,
                used_observation, used_community,
                observation_created_count, observation_regenerated_count,
                created_at, updated_at
            ) VALUES (
                source.activity_date, source.user_id, source.first_active_at, source.last_active_at,
                source.used_observation, source.used_community,
                source.observation_created_count, source.observation_regenerated_count,
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            """;

    private final JdbcTemplate jdbcTemplate;
    private final boolean postgres;

    public JdbcUserActivityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.postgres = Boolean.TRUE.equals(jdbcTemplate.execute((Connection connection) ->
                connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("PostgreSQL")
        ));
    }

    @Override
    public void recordActive(LocalDate activityDate, Long userId, LocalDateTime occurredAt) {
        upsert(activityDate, userId, occurredAt, false, false, 0, 0);
    }

    @Override
    public void recordObservationCreated(LocalDate activityDate, Long userId, LocalDateTime occurredAt) {
        upsert(activityDate, userId, occurredAt, true, false, 1, 0);
    }

    @Override
    public void recordObservationUsed(LocalDate activityDate, Long userId, LocalDateTime occurredAt) {
        upsert(activityDate, userId, occurredAt, true, false, 0, 0);
    }

    @Override
    public void recordObservationRegenerated(LocalDate activityDate, Long userId, LocalDateTime occurredAt) {
        upsert(activityDate, userId, occurredAt, true, false, 0, 1);
    }

    @Override
    public void recordCommunityUsed(LocalDate activityDate, Long userId, LocalDateTime occurredAt) {
        upsert(activityDate, userId, occurredAt, false, true, 0, 0);
    }

    private void upsert(
            LocalDate activityDate,
            Long userId,
            LocalDateTime occurredAt,
            boolean usedObservation,
            boolean usedCommunity,
            long createdCount,
            long regeneratedCount
    ) {
        jdbcTemplate.update(
                postgres ? POSTGRES_UPSERT : H2_MERGE,
                activityDate,
                userId,
                occurredAt,
                occurredAt,
                usedObservation,
                usedCommunity,
                createdCount,
                regeneratedCount
        );
    }
}
