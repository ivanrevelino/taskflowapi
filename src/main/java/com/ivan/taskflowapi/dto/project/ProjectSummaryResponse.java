package com.ivan.taskflowapi.dto.project;

import com.ivan.taskflowapi.models.enums.ProjectMemberRole;

public record ProjectSummaryResponse(Long id, String name, String description, ProjectMemberRole role) {
}
