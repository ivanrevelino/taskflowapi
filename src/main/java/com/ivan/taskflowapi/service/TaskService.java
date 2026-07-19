package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

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
        validateProjectOwnership(project, owner);

        return Task.builder().title(request.title())
                .description(request.description())
                .project(project)
                .build();
    }

    public List<TaskResponseDTO> findMyTasks(Long projectId) {
        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findByIdOrThrowResourceNotFoundException(projectId);
        validateProjectOwnership(project, owner);
        return repository.findByProjectId(projectId)
                .stream()
                .map(taskMapper::toDTO).toList();
    }

    public Task findByIdOrThrowResourceNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));
    }

    public void delete(Long taskId, Long projectId) {

        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findByIdOrThrowResourceNotFoundException(projectId);

        validateProjectOwnership(project, owner);

        Task taskToBeDeleted = findByIdOrThrowResourceNotFoundException(taskId);

        if (!taskToBeDeleted.getProject().getId().equals(project.getId())) {
            throw new BadRequestException("Task does not belong to this project");
        }

        repository.delete(taskToBeDeleted);
    }

    private static void validateProjectOwnership(Project project, User owner) {
        if (!project.getOwner().getId().equals(owner.getId())){
            throw new UnauthorizedException("\"You don't own this project\"");
        }
    }
}
