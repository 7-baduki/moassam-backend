CREATE TABLE comments (
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    nickname    VARCHAR(50)  NOT NULL,
    content     VARCHAR(3000) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);