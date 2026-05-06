CREATE TABLE observations (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    summary         VARCHAR(1000) NOT NULL,
    age             VARCHAR(20)  NOT NULL,
    curriculum_type VARCHAR(30)  NOT NULL,
    situation       TEXT         NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE observation_sections (
    id             BIGSERIAL PRIMARY KEY,
    observation_id BIGINT      NOT NULL,
    section_type   VARCHAR(40) NOT NULL,
    content        TEXT        NOT NULL,
    display_order  int         NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_observations_user_id_id
    ON observations(user_id, id DESC);

CREATE INDEX idx_observation_sections_observation_id
    ON observation_sections(observation_id);