package com.example.deploy.domain.user.model.entity;


import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Enterpriser extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String company;
}
