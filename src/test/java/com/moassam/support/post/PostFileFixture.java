package com.moassam.support.post;

import com.moassam.post.domain.post.FileType;
import com.moassam.post.domain.post.PostFile;
import org.springframework.test.util.ReflectionTestUtils;

public class PostFileFixture {

    public static PostFile createFile(Long fileId, Long postId) {
        PostFile file = PostFile.create(
                postId,
                "test.pdf",
                "https://example.com/test.pdf",
                1024L,
                FileType.FILE
        );

        ReflectionTestUtils.setField(file, "id", fileId);

        return file;
    }

    public static PostFile createEditorImage(Long fileId, Long postId) {
        PostFile file = PostFile.create(
                postId,
                "image.png",
                "https://example.com/image.png",
                2048L,
                FileType.IMAGE
        );

        ReflectionTestUtils.setField(file, "id", fileId);

        return file;
    }
}
