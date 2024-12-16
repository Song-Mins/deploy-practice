package com.example.deploy.domain.Image.repository;


import com.example.deploy.domain.Image.entity.ImageFile;
import com.example.deploy.domain.Image.entity.enums.ContentType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByContentTypeAndContentId(ContentType contentType, Long contentId);

    ImageFile findByFileName(String fileName);
}
