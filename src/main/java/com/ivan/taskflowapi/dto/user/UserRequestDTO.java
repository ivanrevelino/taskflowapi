package com.ivan.taskflowapi.dto.user;

import com.ivan.taskflowapi.models.enums.UserRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank
        @Schema(description = "This is the user name", example = "Satoru Gojo") String name,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank UserRoles role) {}