package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.exception.UnauthorizedException;
import com.ivan.taskflowapi.mapper.TaskMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.repository.TaskRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Transactional
    public Task create(@Valid  TaskRequestDTO request, Long projectId) {
        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findByIdOrThrowResourceNotFoundException(projectId);
        verificateIfProjectBelongsToUser(project, owner);

        return Task.builder().title(request.title())
                .description(request.description())
                .project(project)
                .build();
    }

    public Task findByIdOrThrowResourceNotFoundException(Long id) {

        User user = userService.getAuthenticatedUser();

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));
    }

    public void delete(Long id) {
        Task taskToBeDeleted = findByIdOrThrowResourceNotFoundException(id);
        repository.delete(taskToBeDeleted);
    }

    private static void verificateIfProjectBelongsToUser(Project project, User owner) {
        if (project.getOwner().getId().equals(owner.getId())) throw new UnauthorizedException("\"You don't own this project\"");
    }
}
