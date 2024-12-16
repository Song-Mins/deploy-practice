package com.example.deploy.domain.user.model.request;

public record PasswordChangeRequest(String email, String name, String impId, String password) {}
