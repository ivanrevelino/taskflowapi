package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.invite.InviteResponseDTO;
import com.ivan.taskflowapi.models.ProjectInvite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectInviteMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "inviter.id", target = "inviterId")
    InviteResponseDTO toDTO(ProjectInvite invite);
}
