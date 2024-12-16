package com.example.deploy.domain.admin.model.request;

import jakarta.validation.constraints.NotNull;

public record UpdateTriggerRequest(@NotNull String newCronExpression) {}
