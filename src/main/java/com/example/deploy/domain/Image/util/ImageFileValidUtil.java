package com.example.deploy.domain.Image.util;


import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidUtil { // 이미지 형식 유효성 검증

    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "bmp", "gif", "tiff", "webp", "svg");

    public static boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return false;
        }
        String fileExtension = getFileExtension(file.getOriginalFilename());
        return ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
}
