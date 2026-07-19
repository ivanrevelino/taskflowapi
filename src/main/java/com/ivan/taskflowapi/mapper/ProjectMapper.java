package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.models.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectRequestDTO request);
    ProjectResponseDTO toDTO(Project project);
}
