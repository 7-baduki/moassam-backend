package com.moassam.post.domain;

public record FileUploadRequest(
        String fileName,
        FileType fileType
) {
}
