package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.project.ProjectSummaryResponse;
import com.ivan.taskflowapi.models.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "id", source = "project.id")
    @Mapping(target = "name", source = "project.name")
    @Mapping(target = "description", source = "project.description")
    ProjectSummaryResponse toDto(ProjectMember projectMember);
}
