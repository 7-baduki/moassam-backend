CREATE TABLE admin_accounts(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_admin_accounts_username UNIQUE (username)
);

CREATE TABLE admin_refresh_tokens
(
    id               BIGSERIAL PRIMARY KEY,
    admin_account_id BIGINT       NOT NULL,
    token            VARCHAR(512) NOT NULL,
    expires_at       TIMESTAMP    NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_admin_refresh_tokens_token UNIQUE (token),
    CONSTRAINT uk_admin_refresh_tokens_admin_account_id UNIQUE(admin_account_id)
);

