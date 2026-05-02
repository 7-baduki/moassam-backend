package com.moassam.post.adapter.web.dto.post;

import com.moassam.post.domain.post.FileType;
import com.moassam.post.domain.post.PostFile;

public record PostFileResponse(
        Long fileId,
        String originalName,
        String url,
        long size,
        FileType fileType
) {
    public static PostFileResponse from(PostFile file) {
        return new PostFileResponse(
                file.getId(),
                file.getOriginalName(),
                file.getUrl(),
                file.getSize(),
                file.getFileType()
        );
    }
}
