package com.ivan.taskflowapi.mapper.manual_mapper;

import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.mapper.UserMapper;
import com.ivan.taskflowapi.models.ProjectMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberMapper {

    private final UserMapper userMapper;

    public ProjectMemberResponseDTO toDTO(ProjectMember projectMember) {

        if (projectMember == null) throw new RuntimeException("Error while mapping ProjectMember");
        log.warn("Error while mapping ProjectMember");

        ProjectMemberResponseDTO responseDTO = new ProjectMemberResponseDTO();

        responseDTO.setId(projectMember.getId());
        responseDTO.setUser(userMapper.toDTO(projectMember.getUser()));
        responseDTO.setInvitedBy(userMapper.toDTO(projectMember.getInvitedBy()));
        responseDTO.setJoinedAt(projectMember.getJoinedAt());
        responseDTO.setRole(projectMember.getRole());

        return responseDTO;
    }

    public List<ProjectMemberResponseDTO> toListDTO(List<ProjectMember> projectMembers) {

        if (projectMembers == null || projectMembers.isEmpty()) {
            log.warn("Cannot convert to DTO");
        }

        List<ProjectMemberResponseDTO> response = new ArrayList<>();

        for (ProjectMember projectMember : projectMembers) {

            response.add(toDTO(projectMember));

        }

        return response;
    }


}
