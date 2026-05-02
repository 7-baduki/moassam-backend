package com.moassam.post.domain.post;

public record FileUploadRequest(
        String fileName,
        FileType fileType
) {
}
