package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.UnauthorizedException;
import com.ivan.taskflowapi.mapper.ProjectMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    @Transactional
    public Project create(@Valid  ProjectRequestDTO request) {
        User owner = userService.getAuthenticatedUser();

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(owner);

        Project entity = projectMapper.toEntity(request);
        return repository.save(entity);
    }

    public Project findByIdOrThrowResourceNotFoundException(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid argument");

        User owner = userService.getAuthenticatedUser();
        Project project = repository.findById(id).orElseThrow(() -> new BadRequestException("Project not found"));

        verificateIfUserIsOwnerOfTheProject(project, owner);

        return project;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Project> findByOwnerId(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid argument");
        User user = userService.findByIdOrElseThrowResourceNotFoundException(id);
        return repository.findByOwner(user);
    }

    public void delete(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid argument");

        Project project = findByIdOrThrowResourceNotFoundException(id);
        User owner = userService.getAuthenticatedUser();

        verificateIfUserIsOwnerOfTheProject(project, owner);

        repository.delete(project);
    }

    private static void verificateIfUserIsOwnerOfTheProject(Project project, User owner) {
        if ((project.getOwner().getId().equals(owner.getId()))) {
            throw new UnauthorizedException("You're not authorized to execute this function");
        }
    }
}
