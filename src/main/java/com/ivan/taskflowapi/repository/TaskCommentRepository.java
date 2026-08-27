package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    Optional<TaskComment> findByIdAndTaskId(Long id, Long taskId);
}
