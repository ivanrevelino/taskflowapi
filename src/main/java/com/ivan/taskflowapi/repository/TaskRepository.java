package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
