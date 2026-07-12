package com.commercecore.api.media.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Service managing uploads to DigitalOcean Spaces.
 */
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${app.aws.bucket-name}")
    private String bucketName;

    @Value("${app.aws.cdn-url}")
    private String cdnUrl;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads a file to DigitalOcean Spaces and returns its metadata map containing key and cdn url.
     */
    public UploadResult uploadFile(MultipartFile file, String folderPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generate unique key to prevent collisions
        String path = (folderPath == null || folderPath.trim().isEmpty()) ? "" : folderPath.trim() + "/";
        String key = path + UUID.randomUUID() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ) // Make file publicly readable
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String url = cdnUrl + "/" + key;
        return new UploadResult(key, url, file.getSize(), file.getContentType(), originalFilename);
    }

    public record UploadResult(String key, String url, long size, String mimeType, String originalFilename) {}

}
