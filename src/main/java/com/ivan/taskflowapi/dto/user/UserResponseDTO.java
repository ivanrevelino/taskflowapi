package com.ivan.taskflowapi.dto.user;

import com.ivan.taskflowapi.models.enums.UserRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Public user profile.")
public record UserResponseDTO (
        @Schema(description = "User identifier.", example = "1") @NotBlank Long id,
        @Schema(description = "Display name.", example = "Sam Member") @NotBlank String name,
        @Schema(description = "Unique username.", example = "member01") @NotBlank String username,
        @Schema(description = "Account creation date and time.", example = "2025-01-15T09:30:00")
        @NotBlank LocalDateTime createdAt,
        @Schema(description = "User role.", example = "USER") @NotBlank UserRoles role
){}
