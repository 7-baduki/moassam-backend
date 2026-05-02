package com.moassam.post.application.required;

import com.moassam.post.domain.Post;
import com.moassam.post.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post save(Post post);
}
