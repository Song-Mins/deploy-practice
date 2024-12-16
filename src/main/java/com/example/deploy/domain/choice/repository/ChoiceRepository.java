package com.example.deploy.domain.choice.repository;


import com.example.deploy.domain.choice.model.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {}
