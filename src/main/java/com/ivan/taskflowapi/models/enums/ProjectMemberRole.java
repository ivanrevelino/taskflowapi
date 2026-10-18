package com.ivan.taskflowapi.models.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User's role within a project.")
public enum ProjectMemberRole {

    OWNER, ADMIN, MEMBER

}
