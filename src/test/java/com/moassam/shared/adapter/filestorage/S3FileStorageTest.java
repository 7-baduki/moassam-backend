package com.moassam.shared.adapter.filestorage;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class S3FileStorageTest {

    private final S3Client s3Client = mock(S3Client.class);
    private final CloudFrontClient cloudFrontClient = mock(CloudFrontClient.class);
    private final S3FileStorage fileStorage = new S3FileStorage(s3Client, cloudFrontClient);

    S3FileStorageTest() {
        ReflectionTestUtils.setField(fileStorage, "bucket", "moassam-dev-assets");
        ReflectionTestUtils.setField(fileStorage, "publicBaseUrl", "https://assets-dev.example.com");
        ReflectionTestUtils.setField(fileStorage, "distributionId", "EDEV123");
    }

    @Test
    void uploadsToConfiguredBucketAndReturnsCloudFrontUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notice.png",
                "image/png",
                "image-content".getBytes()
        );

        String url = fileStorage.upload(file, "posts");

        assertThat(url)
                .startsWith("https://assets-dev.example.com/posts/")
                .endsWith("_notice.png");

        then(s3Client).should().putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deletesOnlyConfiguredCloudFrontUrlAndInvalidatesSamePath() {
        fileStorage.delete("https://assets-dev.example.com/posts/example.png");

        then(s3Client).should().deleteObject(argThat((DeleteObjectRequest request) ->
                request.bucket().equals("moassam-dev-assets")
                        && request.key().equals("posts/example.png")
        ));
        then(cloudFrontClient).should().createInvalidation(any(CreateInvalidationRequest.class));
    }

    @Test
    void rejectsDeletionForAnotherHost() {
        assertThatThrownBy(() -> fileStorage.delete("https://example.com/posts/example.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
