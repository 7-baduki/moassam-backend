package com.moassam.shared.adapter.filestorage;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GcpFileStorage implements FileStorage{

    private final Storage storage;

    @Value("${gcp.storage.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String directory) {

        String key = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            BlobId blobId = BlobId.of(bucket, key);

            BlobInfo build = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(build, file.getBytes());

            return String.format(
                    "https://storage.googleapis.com/%s/%s",
                    bucket,
                    key
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다." + key, e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        String prefix = "https://storage.googleapis.com/" + bucket + "/";

        String key = fileUrl.replace(prefix, "");

        storage.delete(bucket, key);
    }
}
