package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User user);
}
