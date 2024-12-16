package com.example.deploy.domain.Image.service;


import com.example.deploy.domain.Image.entity.ImageFile;
import com.example.deploy.domain.Image.entity.enums.ContentType;
import com.example.deploy.domain.Image.exception.S3Exception;
import com.example.deploy.domain.Image.exception.errortype.S3ErrorCode;
import com.example.deploy.domain.Image.repository.ImageFileRepository;
import com.example.deploy.domain.Image.util.ImageFileValidUtil;
import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.global.type.S3PathPrefixType;
import com.example.deploy.global.util.S3Util;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;
    private final S3Util s3Util;

    public String validateAndUploadImage(MultipartFile imageFile, S3PathPrefixType pathPrefixType) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new CampaignException(CampaignErrorCode.IMAGE_REQUIRED);
        }
        if (!ImageFileValidUtil.isValidImageFile(imageFile)) {
            throw new S3Exception(S3ErrorCode.INVALID_IMAGE_FILE);
        }
        return s3Util.saveImage(imageFile, pathPrefixType.toString());
    }

    public String getImageUrl(String imageName, S3PathPrefixType pathPrefixType) {
        return s3Util.selectImage(imageName, pathPrefixType.toString());
    }

    public String selectImage(String imageUrl, S3PathPrefixType pathPrefixType) {
        return s3Util.selectImage(imageUrl, pathPrefixType.toString());
    }

    public void saveImageFiles(
            List<MultipartFile> imageFiles,
            ContentType contentType,
            Long id,
            S3PathPrefixType s3PathPrefixType) {
        if (imageFiles == null || imageFiles.isEmpty()) return;

        imageFiles.stream()
                .filter(Objects::nonNull)
                .filter(imageFile -> !imageFile.isEmpty())
                .forEach(
                        imageFile -> {
                            String fileName = validateAndUploadImage(imageFile, s3PathPrefixType);
                            String url = s3Util.selectImage(fileName, s3PathPrefixType.toString());
                            ImageFile file =
                                    ImageFile.builder()
                                            .contentType(contentType)
                                            .contentId(id)
                                            .fileName(fileName)
                                            .imageUrl(url)
                                            .build();
                            imageFileRepository.save(file);
                        });
    }

    public void deleteImageFiles(List<String> fileNames, S3PathPrefixType s3PathPrefixType) {
        for (String fileName : fileNames) {
            s3Util.deleteImage(fileName, s3PathPrefixType.toString());
            ImageFile file = imageFileRepository.findByFileName(fileName);
            imageFileRepository.delete(file);
        }
    }

    public List<String> findImageUrls(Long postId, ContentType contentType) {
        List<ImageFile> imageFiles =
                imageFileRepository.findByContentTypeAndContentId(contentType, postId);
        return imageFiles.stream().map(ImageFile::getImageUrl).toList();
    }
}
