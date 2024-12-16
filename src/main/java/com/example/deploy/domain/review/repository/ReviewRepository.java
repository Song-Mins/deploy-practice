package com.example.deploy.domain.review.repository;


import com.example.deploy.domain.review.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {}
