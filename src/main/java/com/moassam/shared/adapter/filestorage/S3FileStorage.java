package com.moassam.shared.adapter.filestorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String directory) {
        String fileName = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + fileName, e);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    @Override
    public void delete(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
        amazonS3.deleteObject(bucket, key);
    }
}
