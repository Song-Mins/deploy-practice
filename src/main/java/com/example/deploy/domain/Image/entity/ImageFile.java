package com.example.deploy.domain.Image.entity;


import com.example.deploy.domain.Image.entity.enums.ContentType;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ImageFile extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private Long contentId;

    private String fileName;

    private String imageUrl;
}
