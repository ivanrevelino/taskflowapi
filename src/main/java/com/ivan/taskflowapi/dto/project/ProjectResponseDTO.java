package com.ivan.taskflowapi.dto.project;

import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Project details returned by the API.")
public class ProjectResponseDTO {

    @Schema(description = "Project identifier.", example = "1")
    private Long id;
    @Schema(description = "Project name.", example = "Website redesign")
    private String name;
    @Schema(description = "Project description.", example = "Plan and deliver the new company website.")
    private String description;
    @Schema(description = "Project owner.")
    private UserResponseDTO owner;
    @Schema(description = "Members of the project.")
    private List<ProjectMemberResponseDTO> members;

    public ProjectResponseDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
