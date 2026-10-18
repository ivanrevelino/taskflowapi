package com.ivan.taskflowapi.dto.user;

import com.ivan.taskflowapi.models.enums.UserRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User details.")
public record UserRequestDTO(
        @NotBlank
        @Schema(description = "Display name.", example = "Sam Member", requiredMode = Schema.RequiredMode.REQUIRED) String name,
        @NotBlank
        @Schema(description = "Unique username.", example = "member01", requiredMode = Schema.RequiredMode.REQUIRED) String username,
        @NotBlank
        @Schema(description = "Account password.", example = "P@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED) String password,
        @NotBlank
        @Schema(description = "Assigned user role.", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED) UserRoles role) {}