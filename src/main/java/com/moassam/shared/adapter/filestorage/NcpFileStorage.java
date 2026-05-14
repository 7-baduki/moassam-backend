package com.moassam.shared.adapter.filestorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NcpFileStorage implements FileStorage {

    private final AmazonS3 amazonS3;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String directory) {
        String key = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest request = new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(request);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다: " + key, e);
        }

        return amazonS3.getUrl(bucket, key).toString();
    }



    @Override
    public void delete(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);

        amazonS3.deleteObject(bucket, key);
    }
}
