package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.project_member.AddMemberRequestDTO;
import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.manual_mapper.ProjectMemberMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository repository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserService userService;

    public Page<ProjectMember> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void addOwner(Project project, User authenticatedUser) {
        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .user(authenticatedUser)
                .role(ProjectMemberRole.ADMIN)
                .build();
        repository.save(projectMember);
    }

    public ProjectMemberResponseDTO addMember(Long projectId, AddMemberRequestDTO request) {
        User owner = userService.getAuthenticatedUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        validateProjectOwnerShip(project, owner);

        User member = userService.findById(request.id());

        if (owner.equals(member)) {
            throw new BadRequestException("You cannot invite your self");
        }

        if (repository.existsByProjectIdAndUserId(projectId, request.id())) {
            throw new BadRequestException("User already is a member");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .role(ProjectMemberRole.MEMBER)
                .user(member)
                .project(project)
                .invitedBy(owner)
                .build();
        ProjectMember saved = repository.save(projectMember);
        return projectMemberMapper.toDTO(projectMember);
    }

    private static void validateProjectOwnerShip(Project project, User owner) {
        if (!project.getOwner().getId().equals(owner.getId())) {
            throw new ForbiddenException("This is not your project man");
        }
    }
}
