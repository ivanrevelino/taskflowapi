package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectRequestDTO request);
    ProjectResponseDTO toDTO(Project project);

    void updateFromDto(ProjectRequestDTO request, @MappingTarget Project entity);
}
