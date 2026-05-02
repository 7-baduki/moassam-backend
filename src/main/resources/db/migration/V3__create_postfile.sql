CREATE TABLE post_files (
    id             BIGSERIAL PRIMARY KEY,
    post_id         BIGINT        NOT NULL,
    original_name   VARCHAR(255)  NOT NULL,
    url             VARCHAR(1000) NOT NULL,
    size            BIGINT        NOT NULL,
    file_type       VARCHAR(20)   NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_post_files_post_id ON post_files(post_id);