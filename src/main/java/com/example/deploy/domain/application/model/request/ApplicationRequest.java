package com.example.deploy.domain.application.model.request;

import jakarta.validation.constraints.NotBlank;
import org.antlr.v4.runtime.misc.NotNull;

public record ApplicationRequest(@NotNull Long campaignId, @NotBlank String message) {}
