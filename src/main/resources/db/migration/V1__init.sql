CREATE TABLE users (
    id               BIGSERIAL PRIMARY KEY,
    provider         VARCHAR(20)  NOT NULL,
    provider_id      VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    nickname         VARCHAR(50),
    role             VARCHAR(20)  NOT NULL,
    profile_image_url VARCHAR(500),
    deleted_at       TIMESTAMP,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_provider UNIQUE (provider, provider_id)
);

CREATE TABLE refresh_tokens (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token)
);