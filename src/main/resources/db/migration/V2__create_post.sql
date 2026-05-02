CREATE TABLE posts (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    title          VARCHAR(200) NOT NULL,
    content        TEXT         NOT NULL,
    category       VARCHAR(20)  NOT NULL,
    age            VARCHAR(20),
    resource_type  VARCHAR(30),
    head_tag       VARCHAR(30),
    view_count     BIGINT       NOT NULL DEFAULT 0,
    like_count     BIGINT       NOT NULL DEFAULT 0,
    comment_count  BIGINT       NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_category ON posts(category);