package com.ivan.taskflowapi.dto.project_member;

import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponseDTO {
    private Long id;
    private UserResponseDTO user;
    private UserResponseDTO invitedBy;
    private LocalDateTime joinedAt;
    private ProjectMemberRole role;
}