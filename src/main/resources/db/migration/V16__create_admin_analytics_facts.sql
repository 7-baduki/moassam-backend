CREATE TABLE user_daily_activities (
    activity_date                   DATE      NOT NULL,
    user_id                         BIGINT    NOT NULL,
    first_active_at                 TIMESTAMP NOT NULL,
    last_active_at                  TIMESTAMP NOT NULL,
    used_observation                BOOLEAN   NOT NULL DEFAULT FALSE,
    used_community                  BOOLEAN   NOT NULL DEFAULT FALSE,
    observation_created_count       BIGINT    NOT NULL DEFAULT 0,
    observation_regenerated_count   BIGINT    NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (activity_date, user_id),
    CONSTRAINT ck_user_daily_activity_created_count
        CHECK (observation_created_count >= 0),
    CONSTRAINT ck_user_daily_activity_regenerated_count
        CHECK (observation_regenerated_count >= 0)
);

CREATE INDEX idx_user_daily_activities_user_date
    ON user_daily_activities(user_id, activity_date);

CREATE TABLE user_lifecycle_events (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    event_type          VARCHAR(30)  NOT NULL,
    had_meaningful_use  BOOLEAN,
    occurred_at         TIMESTAMP    NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_user_lifecycle_event_type
        CHECK (event_type IN ('SIGNUP', 'REACTIVATION', 'WITHDRAWAL'))
);

CREATE INDEX idx_user_lifecycle_events_occurred_type
    ON user_lifecycle_events(occurred_at, event_type);
CREATE INDEX idx_user_lifecycle_events_user_occurred
    ON user_lifecycle_events(user_id, occurred_at);

CREATE TABLE observation_generation_usages (
    id                  BIGSERIAL PRIMARY KEY,
    correlation_id      VARCHAR(36)  NOT NULL,
    user_id             BIGINT       NOT NULL,
    operation           VARCHAR(30)  NOT NULL,
    provider_status     VARCHAR(30)  NOT NULL,
    business_status     VARCHAR(30)  NOT NULL,
    model               VARCHAR(100),
    input_tokens        BIGINT,
    output_tokens       BIGINT,
    total_tokens        BIGINT,
    duration_ms         BIGINT       NOT NULL,
    error_category      VARCHAR(50),
    occurred_at         TIMESTAMP    NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_observation_generation_usages_correlation UNIQUE (correlation_id),
    CONSTRAINT ck_observation_generation_duration CHECK (duration_ms >= 0),
    CONSTRAINT ck_observation_generation_input_tokens CHECK (input_tokens IS NULL OR input_tokens >= 0),
    CONSTRAINT ck_observation_generation_output_tokens CHECK (output_tokens IS NULL OR output_tokens >= 0),
    CONSTRAINT ck_observation_generation_total_tokens CHECK (total_tokens IS NULL OR total_tokens >= 0)
);

CREATE INDEX idx_observation_generation_usages_occurred
    ON observation_generation_usages(occurred_at, operation, provider_status);

CREATE TABLE analytics_collection_state (
    metric_key       VARCHAR(50) PRIMARY KEY,
    available_from   DATE        NOT NULL,
    gap_detected     BOOLEAN     NOT NULL DEFAULT FALSE,
    updated_at       TIMESTAMP   NOT NULL DEFAULT NOW()
);

INSERT INTO analytics_collection_state(metric_key, available_from)
VALUES ('USER_ACTIVITY', CURRENT_DATE),
       ('USER_LIFECYCLE', CURRENT_DATE),
       ('AI_USAGE', CURRENT_DATE);

INSERT INTO user_lifecycle_events(user_id, event_type, occurred_at)
SELECT id, 'SIGNUP', created_at FROM users;

INSERT INTO user_lifecycle_events(user_id, event_type, had_meaningful_use, occurred_at)
SELECT u.id,
       'WITHDRAWAL',
       CASE
           WHEN EXISTS (SELECT 1 FROM posts p WHERE p.user_id = u.id)
               OR EXISTS (SELECT 1 FROM comments c WHERE c.user_id = u.id)
               OR EXISTS (SELECT 1 FROM observations o WHERE o.user_id = u.id)
               THEN TRUE
           ELSE NULL
       END,
       u.deleted_at
FROM users u
WHERE u.deleted_at IS NOT NULL;

ALTER TABLE observations
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE INDEX idx_observations_created_at_id
    ON observations(created_at DESC, id DESC);

CREATE INDEX idx_posts_created_at_id
    ON posts(created_at DESC, id DESC);
