package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.DocumentUploadRequest;
import ai.anamaya.service.oms.core.enums.DocumentBucketType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DocumentService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    public String uploadFile(CallerContext callerContext, DocumentUploadRequest request) throws IOException {
        String filename = generateFileName(request.getType());
        String key = request.getType().getPath()+"/"+filename;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(request.getFile().getContentType())
            .build();

        s3Client.putObject(objectRequest, RequestBody.fromBytes(request.getFile().getBytes()));

        return key;
    }

    public String generateDownloadUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        GetObjectPresignRequest presignRequest =
            GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest =
            s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    private String generateFileName(DocumentBucketType type) {
        return (type.name() + "_" + System.currentTimeMillis());
    }

}