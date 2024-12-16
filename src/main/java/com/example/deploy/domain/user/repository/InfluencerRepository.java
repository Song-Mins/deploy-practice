package com.example.deploy.domain.user.repository;


import com.example.deploy.domain.user.model.entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfluencerRepository extends JpaRepository<Influencer, Long> {}
