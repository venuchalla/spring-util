package com.example.springutil.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseWrapper<T>(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String requstId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String apiName,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String apiVersion,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Integer status,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) T Response) {}
