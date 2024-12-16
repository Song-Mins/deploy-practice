package com.example.deploy.domain.user.repository;


import com.example.deploy.domain.user.model.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnsRepository extends JpaRepository<Sns, Long> {}
