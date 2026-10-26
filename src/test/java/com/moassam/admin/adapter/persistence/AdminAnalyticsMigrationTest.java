package com.moassam.admin.adapter.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class AdminAnalyticsMigrationTest {

    @Test
    void migrationKeepsUnknownHistoricalWithdrawalUseNullableAndAddsRecentPostIndex() throws Exception {
        try (var connection = DriverManager.getConnection(
                "jdbc:h2:mem:admin-analytics-migration;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
        )) {
            try (var statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE users (
                            id BIGINT PRIMARY KEY,
                            created_at TIMESTAMP NOT NULL,
                            deleted_at TIMESTAMP
                        )
                        """);
                statement.execute("""
                        CREATE TABLE posts (
                            id BIGINT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            created_at TIMESTAMP NOT NULL
                        )
                        """);
                statement.execute("CREATE TABLE comments (id BIGINT PRIMARY KEY, user_id BIGINT NOT NULL)");
                statement.execute("""
                        CREATE TABLE observations (
                            id BIGINT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            created_at TIMESTAMP NOT NULL
                        )
                        """);
                statement.execute("""
                        INSERT INTO users(id, created_at, deleted_at) VALUES
                            (1, TIMESTAMP '2026-01-01 00:00:00', TIMESTAMP '2026-10-01 00:00:00'),
                            (2, TIMESTAMP '2026-01-02 00:00:00', TIMESTAMP '2026-10-02 00:00:00')
                        """);
                statement.execute("""
                        INSERT INTO posts(id, user_id, created_at)
                        VALUES (10, 2, TIMESTAMP '2026-05-01 00:00:00')
                        """);
            }

            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/migration/V16__create_admin_analytics_facts.sql")
            );

            try (var statement = connection.createStatement()) {
                try (var resultSet = statement.executeQuery("""
                        SELECT user_id, had_meaningful_use
                        FROM user_lifecycle_events
                        WHERE event_type = 'WITHDRAWAL'
                        ORDER BY user_id
                        """)) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getLong("user_id")).isEqualTo(1L);
                    assertThat(resultSet.getObject("had_meaningful_use")).isNull();
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getLong("user_id")).isEqualTo(2L);
                    assertThat(resultSet.getBoolean("had_meaningful_use")).isTrue();
                }

                try (var resultSet = statement.executeQuery("""
                        SELECT COUNT(*)
                        FROM information_schema.indexes
                        WHERE table_name = 'POSTS'
                          AND index_name = 'IDX_POSTS_CREATED_AT_ID'
                        """)) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getLong(1)).isEqualTo(1L);
                }
            }
        }
    }
}
