package com.moassam.shared.adapter.filestorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch;
import software.amazon.awssdk.services.cloudfront.model.Paths;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;
    private final CloudFrontClient cloudFrontClient;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.public-base-url}")
    private String publicBaseUrl;

    @Value("${storage.cloudfront-distribution-id}")
    private String distributionId;

    @Override
    public String upload(MultipartFile file, String directory) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일 이름이 필요합니다.");
        }
        originalFilename = Path.of(originalFilename.replace('\\', '/')).getFileName().toString();
        String key = directory + "/" + UUID.randomUUID() + "_" + originalFilename;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            return publicBaseUrl().resolve(key).toString();
        } catch (IOException exception) {
            throw new IllegalStateException("파일 업로드에 실패했습니다: " + key, exception);
        }
    }

    @Override
    public void delete(String fileUrl) {
        String key = keyFrom(fileUrl);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());

        cloudFrontClient.createInvalidation(CreateInvalidationRequest.builder()
                .distributionId(distributionId)
                .invalidationBatch(InvalidationBatch.builder()
                        .callerReference(UUID.randomUUID().toString())
                        .paths(Paths.builder().quantity(1).items("/" + key).build())
                        .build())
                .build());
    }

    private URI publicBaseUrl() {
        return URI.create(publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/");
    }

    private String keyFrom(String fileUrl) {
        URI fileUri = URI.create(fileUrl);
        URI baseUri = publicBaseUrl();

        if (!baseUri.getHost().equalsIgnoreCase(fileUri.getHost()) || fileUri.getRawPath() == null) {
            throw new IllegalArgumentException("허용되지 않은 파일 URL입니다.");
        }

        String key = URLDecoder.decode(fileUri.getRawPath(), StandardCharsets.UTF_8).replaceFirst("^/", "");
        if (key.isBlank() || key.contains("..")) {
            throw new IllegalArgumentException("허용되지 않은 파일 URL입니다.");
        }
        return key;
    }
}
