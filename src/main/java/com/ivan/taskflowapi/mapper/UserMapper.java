package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.user.UserRequestDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO requestDTO);
    UserResponseDTO toDTO(User user);
}
