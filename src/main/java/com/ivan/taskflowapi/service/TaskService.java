package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.mapper.TaskMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.TaskRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository repository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final UserService userService;

    public Page<TaskResponseDTO> findAll(Long projectId, Pageable pageable) {

        User user = userService.getAuthenticatedUser();
        Project project = projectService.findById(projectId);

        ensureUserIsMember(project, user);

        return repository.findByProjectId(projectId, pageable).map(taskMapper::toDTO);
    }

    @Transactional
    public TaskResponseDTO create(@Valid TaskRequestDTO request, Long projectId) {

        User user = userService.getAuthenticatedUser();
        Project project = projectService.findById(projectId);

        ProjectMember member = projectMemberRepository.findByProjectIdAndUser(projectId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!(member.getRole() == ProjectMemberRole.OWNER || member.getRole() == ProjectMemberRole.ADMIN)) {
            throw new ForbiddenException();
        }

        Task taskToBeSaved = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.TO_DO)
                .project(project).build();

        Task saved = repository.save(taskToBeSaved);

        log.info("CREATION SUCCESS - User(id: {}, username: {}) created Task(id: {}, title: {}) for Project(id: {}, name: {})",
                user.getId(), user.getUsername(), saved.getId(), saved.getTitle(), project.getId(), project.getName());

        return taskMapper.toDTO(saved);
    }

    @Transactional
    public TaskResponseDTO update(Long projectId, Long taskId, TaskRequestDTO request) {
        User user = userService.getAuthenticatedUser();
        Project project = projectService.findById(projectId);

        ProjectMember member = projectMemberRepository.findByProjectIdAndUser(project.getId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getRole() != ProjectMemberRole.ADMIN && member.getRole() != ProjectMemberRole.OWNER) {
            throw new ForbiddenException();
        }

        Task task = findById(taskId);
        taskMapper.updateFromDTO(request, task);

        Task saved = repository.save(task);
        log.info("UPDATE SUCCESS - User(id: {}, username: {}) updated Task(id: {}, title: {}) for Project(id: {}, name: {})",
                user.getId(), user.getUsername(), saved.getId(), saved.getTitle(), project.getId(), project.getName());

        return new TaskResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus());
    }

    public Task findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));
    }

    @Transactional
    public TaskResponseDTO completeTask(Long projectId, Long taskId) {
        User user = userService.getAuthenticatedUser();
        Project project = projectService.findById(projectId);

        ensureUserIsMember(project, user);

        Task task = findById(taskId);

        ensureTaskBelongsToProject(task, project);

        task.setStatus(TaskStatus.COMPLETED);
        Task saved = repository.save(task);
        return new TaskResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus());
    }

    public List<Task> findByStatus(TaskStatus status, Long projectId) {
        Project project = projectService.findById(projectId);
        User user = userService.getAuthenticatedUser();

        ensureUserIsMember(project, user);

        return repository.findByProjectIdAndStatus(projectId, status);
    }

    public void delete(Long taskId, Long projectId) {
        User user = userService.getAuthenticatedUser();
        Project project = projectService.findById(projectId);

        ProjectMember member = projectMemberRepository.findByProjectIdAndUser(project.getId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getRole() == ProjectMemberRole.MEMBER || member.getRole() == ProjectMemberRole.ADMIN) {
            throw new ForbiddenException();
        }

        Task taskToBeDeleted = findById(taskId);

        ensureTaskBelongsToProject(taskToBeDeleted, project);

        repository.delete(taskToBeDeleted);
    }

    private static void ensureTaskBelongsToProject(Task task, Project project) {
        if (!task.getProject().getId().equals(project.getId())) {
            throw new BadRequestException("Task does not belong to this project");
        }
    }

    private void ensureUserIsMember(Project project, User user) {
        if (!(projectMemberRepository.existsByProjectIdAndUserId(project.getId(), user.getId()))) {
            throw new ResourceNotFoundException("Member not found");
        }
    }
}
