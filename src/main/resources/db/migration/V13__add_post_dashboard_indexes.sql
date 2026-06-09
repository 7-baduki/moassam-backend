CREATE INDEX IF NOT EXISTS idx_posts_category_created_at
    ON posts (category, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_posts_moabang_dashboard
    ON posts (category, post_age, resource_type, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_posts_free_dashboard
    ON posts (category, head_tag, created_at DESC);