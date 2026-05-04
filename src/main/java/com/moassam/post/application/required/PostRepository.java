package com.moassam.post.application.required;

import com.moassam.post.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post save(Post post);
}
