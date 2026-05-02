package com.moassam.post.application.required;

import com.moassam.post.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    PostFile save(PostFile postFile);

    List<PostFile> findAllByPostId(Long postId);

    List<PostFile> findAllByIdInAndPostId(List<Long> fileIds, Long postId);

}
