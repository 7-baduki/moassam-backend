DELETE FROM post_views
WHERE post_id NOT IN (SELECT id FROM posts);

DELETE FROM post_bookmarks
WHERE post_id NOT IN (SELECT id FROM posts);

DELETE FROM post_likes
WHERE post_id NOT IN (SELECT id FROM posts);

DELETE FROM comments
WHERE post_id NOT IN (SELECT id FROM posts);

DELETE FROM post_files
WHERE post_id NOT IN (SELECT id FROM posts);


ALTER TABLE post_files
    ADD CONSTRAINT fk_post_files_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE;

ALTER TABLE post_likes
    ADD CONSTRAINT fk_post_likes_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE;

ALTER TABLE post_bookmarks
    ADD CONSTRAINT fk_post_bookmarks_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE;

ALTER TABLE post_views
    ADD CONSTRAINT fk_post_views_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE;