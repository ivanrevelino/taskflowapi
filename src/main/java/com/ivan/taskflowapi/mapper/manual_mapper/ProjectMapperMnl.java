package com.ivan.taskflowapi.mapper.manual_mapper;

import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.mapper.UserMapper;
import com.ivan.taskflowapi.models.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapperMnl {

    private final UserMapper userMapper;
    private final ProjectMemberMapper projectMemberMapper;

    public List<ProjectResponseDTO> toResponseList(List<Project> projects) {

        List<ProjectResponseDTO> responsesList = new ArrayList<>();

        for (Project project : projects) {

            responsesList.add(new ProjectResponseDTO(
                    project.getId(),
                    project.getName(),
                    project.getDescription(),
                    userMapper.toDTO(project.getOwner()),
                    projectMemberMapper.toListDTO(project.getMembers())
            ));

        }
        return responsesList;
    }

}
