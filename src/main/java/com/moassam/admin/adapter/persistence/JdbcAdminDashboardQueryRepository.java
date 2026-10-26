package com.moassam.admin.adapter.persistence;

import com.moassam.admin.application.required.AdminDashboardQueryRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcAdminDashboardQueryRepository implements AdminDashboardQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAdminDashboardQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long countLifecycleEvents(
            String eventType,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    ) {
        return count("""
                SELECT COUNT(*)
                FROM user_lifecycle_events
                WHERE event_type = ? AND occurred_at >= ? AND occurred_at < ?
                """, eventType, startInclusive, endExclusive);
    }

    @Override
    public long countUnusedWithdrawals(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return count("""
                SELECT COUNT(*)
                FROM user_lifecycle_events
                WHERE event_type = 'WITHDRAWAL'
                  AND had_meaningful_use = FALSE
                  AND occurred_at >= ? AND occurred_at < ?
                """, startInclusive, endExclusive);
    }

    @Override
    public long countSignupUsersWithObservation(
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    ) {
        return count("""
                SELECT COUNT(DISTINCT lifecycle.user_id)
                FROM user_lifecycle_events lifecycle
                WHERE lifecycle.event_type = 'SIGNUP'
                  AND lifecycle.occurred_at >= ? AND lifecycle.occurred_at < ?
                  AND EXISTS (
                      SELECT 1
                      FROM user_daily_activities activity
                      WHERE activity.user_id = lifecycle.user_id
                        AND activity.activity_date >= ? AND activity.activity_date < ?
                        AND activity.observation_created_count > 0
                  )
                """,
                startInclusive,
                endExclusive,
                startInclusive.toLocalDate(),
                endExclusive.toLocalDate()
        );
    }

    @Override
    public long countActiveUsers(LocalDate startInclusive, LocalDate endExclusive) {
        return count("""
                SELECT COUNT(DISTINCT user_id)
                FROM user_daily_activities
                WHERE activity_date >= ? AND activity_date < ?
                """, startInclusive, endExclusive);
    }

    @Override
    public long countObservationCreated(LocalDate startInclusive, LocalDate endExclusive) {
        return count("""
                SELECT COALESCE(SUM(observation_created_count), 0)
                FROM user_daily_activities
                WHERE activity_date >= ? AND activity_date < ?
                """, startInclusive, endExclusive);
    }

    @Override
    public List<DailyCount> findDailyActiveUsers(LocalDate startInclusive, LocalDate endExclusive) {
        return jdbcTemplate.query("""
                        SELECT activity_date, COUNT(DISTINCT user_id) AS active_count
                        FROM user_daily_activities
                        WHERE activity_date >= ? AND activity_date < ?
                        GROUP BY activity_date
                        ORDER BY activity_date
                        """,
                (resultSet, rowNumber) -> new DailyCount(
                        resultSet.getObject("activity_date", LocalDate.class),
                        resultSet.getLong("active_count")
                ),
                startInclusive,
                endExclusive
        );
    }

    @Override
    public long countReturningUsers(
            LocalDate previousStartInclusive,
            LocalDate previousEndExclusive,
            LocalDate recentStartInclusive,
            LocalDate recentEndExclusive
    ) {
        return count("""
                SELECT COUNT(DISTINCT recent.user_id)
                FROM user_daily_activities recent
                WHERE recent.activity_date >= ? AND recent.activity_date < ?
                  AND EXISTS (
                      SELECT 1
                      FROM user_daily_activities previous
                      WHERE previous.user_id = recent.user_id
                        AND previous.activity_date >= ? AND previous.activity_date < ?
                  )
                """,
                recentStartInclusive,
                recentEndExclusive,
                previousStartInclusive,
                previousEndExclusive
        );
    }

    @Override
    public FeatureUsageCounts getFeatureUsage(LocalDate startInclusive, LocalDate endExclusive) {
        return jdbcTemplate.queryForObject("""
                        SELECT
                            COALESCE(SUM(CASE WHEN used_observation = TRUE AND used_community = FALSE THEN 1 ELSE 0 END), 0) observation_only,
                            COALESCE(SUM(CASE WHEN used_observation = TRUE AND used_community = TRUE THEN 1 ELSE 0 END), 0) observation_and_community,
                            COALESCE(SUM(CASE WHEN used_observation = FALSE AND used_community = TRUE THEN 1 ELSE 0 END), 0) community_only,
                            COALESCE(SUM(CASE WHEN used_observation = FALSE AND used_community = FALSE THEN 1 ELSE 0 END), 0) browse_only
                        FROM user_daily_activities
                        WHERE activity_date >= ? AND activity_date < ?
                        """,
                (resultSet, rowNumber) -> new FeatureUsageCounts(
                        resultSet.getLong("observation_only"),
                        resultSet.getLong("observation_and_community"),
                        resultSet.getLong("community_only"),
                        resultSet.getLong("browse_only")
                ),
                startInclusive,
                endExclusive
        );
    }

    @Override
    public GenerationCounts getGenerationCounts(LocalDate startInclusive, LocalDate endExclusive) {
        return jdbcTemplate.queryForObject("""
                        SELECT COALESCE(SUM(observation_created_count), 0) created_count,
                               COALESCE(SUM(observation_regenerated_count), 0) regenerated_count
                        FROM user_daily_activities
                        WHERE activity_date >= ? AND activity_date < ?
                        """,
                (resultSet, rowNumber) -> new GenerationCounts(
                        resultSet.getLong("created_count"),
                        resultSet.getLong("regenerated_count")
                ),
                startInclusive,
                endExclusive
        );
    }

    @Override
    public AiUsageTotals getAiUsage(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return jdbcTemplate.queryForObject("""
                        SELECT COUNT(*) calls,
                               COALESCE(SUM(CASE WHEN business_status = 'SAVED' THEN 1 ELSE 0 END), 0) successes,
                               COALESCE(SUM(CASE WHEN business_status IN ('PROVIDER_FAILED', 'PERSISTENCE_FAILED') THEN 1 ELSE 0 END), 0) failures,
                               COALESCE(SUM(input_tokens), 0) input_tokens,
                               COALESCE(SUM(output_tokens), 0) output_tokens,
                               COALESCE(SUM(total_tokens), 0) total_tokens,
                               AVG(duration_ms) average_duration_ms
                        FROM observation_generation_usages
                        WHERE occurred_at >= ? AND occurred_at < ?
                        """,
                (resultSet, rowNumber) -> new AiUsageTotals(
                        resultSet.getLong("calls"),
                        resultSet.getLong("successes"),
                        resultSet.getLong("failures"),
                        resultSet.getLong("input_tokens"),
                        resultSet.getLong("output_tokens"),
                        resultSet.getLong("total_tokens"),
                        nullableDouble(resultSet, "average_duration_ms")
                ),
                startInclusive,
                endExclusive
        );
    }

    @Override
    public List<RecentPostSummary> findRecentPosts(int limit) {
        return jdbcTemplate.query("""
                        SELECT id, category, title, comment_count, created_at
                        FROM posts
                        ORDER BY created_at DESC, id DESC
                        LIMIT ?
                        """,
                (resultSet, rowNumber) -> new RecentPostSummary(
                        resultSet.getLong("id"),
                        resultSet.getString("category"),
                        resultSet.getString("title"),
                        resultSet.getLong("comment_count"),
                        resultSet.getObject("created_at", LocalDateTime.class)
                ),
                limit
        );
    }

    @Override
    public CollectionState findCollectionState(String metricKey) {
        try {
            return jdbcTemplate.queryForObject("""
                            SELECT available_from, gap_detected
                            FROM analytics_collection_state
                            WHERE metric_key = ?
                            """,
                    (resultSet, rowNumber) -> new CollectionState(
                            resultSet.getObject("available_from", LocalDate.class),
                            resultSet.getBoolean("gap_detected")
                    ),
                    metricKey
            );
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private long count(String sql, Object... arguments) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, arguments);
        return value == null ? 0 : value;
    }

    private Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
