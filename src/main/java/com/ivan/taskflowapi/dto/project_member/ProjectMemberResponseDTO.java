package com.ivan.taskflowapi.dto.project_member;

import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A user's membership in a project.")
public class ProjectMemberResponseDTO {
    @Schema(description = "Membership identifier.", example = "5")
    private Long id;
    @Schema(description = "Project member profile.")
    private UserResponseDTO user;
    @Schema(description = "User who invited the member.")
    private UserResponseDTO invitedBy;
    @Schema(description = "Date and time the user joined.", example = "2025-01-15T09:30:00")
    private LocalDateTime joinedAt;
    @Schema(description = "Member's role in the project.", example = "MEMBER")
    private ProjectMemberRole role;
}