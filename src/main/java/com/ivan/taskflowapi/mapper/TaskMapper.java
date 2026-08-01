package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.models.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskRequestDTO request);
    TaskResponseDTO toDTO(Task task);

    void updateFromDTO(TaskRequestDTO request, @MappingTarget Task task);
}
