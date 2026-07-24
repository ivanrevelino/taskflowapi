package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.mapper.ProjectMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository repository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    public List<ProjectResponseDTO> findMyProjects() {
        User user = userService.getAuthenticatedUser();
        return repository.findByOwner(user).stream().map(projectMapper::toDTO).toList();
    }

    @Transactional
    public Project create(@Valid  ProjectRequestDTO request) {

        User owner = userService.getAuthenticatedUser();

        Project projectToBeSaved = projectMapper.toEntity(request);
        projectToBeSaved.setOwner(owner);

        Project saved = repository.save(projectToBeSaved);

        log.info("CREATION SUCCESS - User(id: {}, username: {}) created Project(id: {}, name: {}, description: {})",
                owner.getId(), owner.getUsername(), saved.getId(), saved.getName(), saved.getDescription());

        return saved;
    }

    public Project findMyProjectById(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid argument");

        User owner = userService.getAuthenticatedUser();
        Project project = repository.findById(id).orElseThrow(() -> new BadRequestException("Project not found"));

        verifyUserIsProjectOwner(project, owner);

        return project;
    }

    @Transactional
    public void deleteWithOwnershipCheck(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid argument");
        Project project = findMyProjectById(id);

        log.info("DELETE SUCCESS - User(id: {}, username: {}) deleted Project(id: {}, name: {})",
                project.getOwner().getId(), project.getOwner().getUsername(), project.getId(), project.getName());

        repository.delete(project);
    }

    public Page<Project> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Project findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Project> findByOwnerId(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid argument");
        User user = userService.findByIdOrElseThrowResourceNotFoundException(id);
        return repository.findByOwner(user);
    }

    private static void verifyUserIsProjectOwner(Project project, User owner) {
        if (!(project.getOwner().getId().equals(owner.getId()))) {
            throw new ForbiddenException("You're not authorized to execute this function");
        }
    }
}
