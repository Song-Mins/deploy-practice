package com.example.deploy.domain.user.service;


import com.example.deploy.domain.Image.service.ImageFileService;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.model.response.UserHeaderResponse;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.type.S3PathPrefixType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageFileService imageFileService;

    @Transactional
    public void update(Long id, MultipartFile imageFile) {

        User user = userRepository.getUserById(id);

        String imageFileName =
                imageFileService.validateAndUploadImage(
                        imageFile, S3PathPrefixType.S3_PROFILE_IMAGE_PATH);
        String imageUrl =
                imageFileService.selectImage(imageFileName, S3PathPrefixType.S3_PROFILE_IMAGE_PATH);

        user.change(imageFileName, imageUrl);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.getUserById(id);
        user.delete();
    }

    @Transactional
    public UserHeaderResponse getHeaderInfo(Long userId) {
        User user = userRepository.getUserById(userId);
        return UserHeaderResponse.of(user);
    }
}
