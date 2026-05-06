CREATE TABLE credit_wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance INTEGER NOT NULL,
    daily_bonus_charged_amount INTEGER NOT NULL,
    last_reset_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_credit_wallets_user_id UNIQUE (user_id)
);