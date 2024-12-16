package com.example.deploy.global.util;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.example.deploy.domain.Image.exception.S3Exception;
import com.example.deploy.domain.Image.exception.errortype.S3ErrorCode;
import com.example.deploy.domain.Image.util.ImageFileValidUtil;
import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucket}") private String bucketName;

    private final AmazonS3 s3Client;

    /**
     * 리소스 저장
     *
     * @param file 저장할 리소스
     * @param path 리소스가 저장될 경로
     * @return 저장된 리소스의 확장자 포함 파일명
     */
    public String saveImage(MultipartFile file, String path) {

        // 유효한 값인지 검증
        if (file == null || file.isEmpty()) {
            throw new CampaignException(CampaignErrorCode.IMAGE_REQUIRED);
        }
        if (!ImageFileValidUtil.isValidImageFile(file)) {
            throw new S3Exception(S3ErrorCode.INVALID_IMAGE_FILE);
        }

        String fileName = System.currentTimeMillis() + "." + extractExtensionName(file);
        String savePath = bucketName + path;

        try {
            s3Client.putObject(savePath, fileName, file.getInputStream(), null);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.IMAGE_UPLOAD_FAILED);
        }
        return CompletableFuture.completedFuture(fileName).join();
    }

    /**
     * 리소스 조회
     *
     * @param fileName 조회할 파일 확장자 포함 이름
     * @param path 파일이 저장된 경로
     * @return fileName 과 일치하는 이름의 리소스 url 반환
     */
    public String selectImage(String fileName, String path) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        String selectPath = bucketName + path;
        s3Client.getObject(new GetObjectRequest(selectPath, fileName));
        URL url = s3Client.getUrl(selectPath, fileName);
        return url.toString();
    }

    /**
     * 리소스 삭제
     *
     * @param fileName 삭제할 파일 확장자 포함 이름
     * @param path 파일이 저장된 경로
     */
    public void deleteImage(String fileName, String path) {
        String deletePath = bucketName + path;
        s3Client.deleteObject(new DeleteObjectRequest(deletePath, fileName));
    }

    /**
     * 확장자명 추출
     *
     * @param file 확장자명 추출할 대상 파일
     * @return 확장자명
     */
    private String extractExtensionName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String[] div = fileName.split("\\.");
        return div[div.length - 1];
    }
}
