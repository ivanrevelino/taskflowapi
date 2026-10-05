package com.ivan.taskflowapi.mapper;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.models.TaskComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TaskCommentMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    TaskCommentResponseDTO toDTO(TaskComment taskComment);
}
