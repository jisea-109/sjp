package com.tinystop.sjp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tinystop.sjp.Exception.CustomException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static com.tinystop.sjp.Type.ErrorCode.FAILED_TO_UPLOAD_IMAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(@Value("${cloud.aws.credentials.access-key}") String accessKey,
                     @Value("${cloud.aws.credentials.secret-key}") String secretKey,
                     @Value("${cloud.aws.region.static}") String region) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public List<String> uploadImages(MultipartFile[] uploadImages, String folder, String page) {
        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile image : uploadImages) {
            if (!image.isEmpty()) {
                String originalFilename = image.getOriginalFilename();
                String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
                String s3Key = folder + "/" + uniqueFilename;

                try {
                    PutObjectRequest putRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(image.getContentType())
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build();

                    s3Client.putObject(putRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
                    uploadedFileNames.add(s3Key);
                } catch (IOException e) {
                    throw new CustomException(FAILED_TO_UPLOAD_IMAGE, page);
                }
            }
        }
        return uploadedFileNames;
    }

    public List<String> getFileUrls(List<String> fileNames) {
        return fileNames.stream()
                .map(fileName -> {
                    if (fileName.startsWith("http")) {
                        return fileName;
                    }
                    return getFileUrl(fileName);
                })
                .collect(Collectors.toList());
    }

    public String getFileUrl(String key) {
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())
                .toExternalForm();
    }

    public void deleteFile(String fileName) {
        try {
            String key = extractKeyFromUrl(fileName);
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (S3Exception e) { // S3 삭제 실패
            System.err.println("S3 오류 코드: " + e.statusCode());
            System.err.println("S3 오류 메시지: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) { // S3 삭제 실패
            System.err.println("S3 오류 메세지: " + e.getMessage());
        }
    }

    private String extractKeyFromUrl(String urlOrKey) {
            if (urlOrKey.startsWith("http")) {
            try {
                URL url = new URL(urlOrKey);
                String encodedKey = url.getPath().substring(1); // "/reviews/..." → "reviews/..."
                return java.net.URLDecoder.decode(encodedKey, java.nio.charset.StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                System.err.println("URL 디코딩 오류: " + e.getMessage());
                return urlOrKey;
            }
        }
        return urlOrKey;
    }
}
