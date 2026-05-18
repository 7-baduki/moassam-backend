ALTER TABLE posts
    ADD COLUMN bookmark_count BIGINT NOT NULL DEFAULT 0;

UPDATE posts p
SET bookmark_count = COALESCE(b.count, 0)
    FROM (
    SELECT post_id, COUNT(*) AS count
    FROM post_bookmarks
    GROUP BY post_id
) b
WHERE p.id = b.post_id;