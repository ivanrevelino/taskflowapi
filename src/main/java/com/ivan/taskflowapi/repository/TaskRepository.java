package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

}
