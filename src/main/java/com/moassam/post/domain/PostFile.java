package com.moassam.post.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFile {

    private Long id;
    private Long postId;
    private String originalName;
    private String url;
    private long size;

    private FileType fileType;

    protected PostFile(Long postId, String originalName, String url, long size, FileType fileType) {
        this.postId = postId;
        this.originalName = originalName;
        this.url = url;
        this.size = size;
        this.fileType = fileType;
    }

    public static PostFile create(
            Long postId,
            String originalName,
            String url,
            long size,
            FileType fileType
    ) {
        return new PostFile(postId, originalName, url, size, fileType);
    }

    public boolean isEditorImage() {
        return fileType == FileType.IMAGE;
    }
}
