package com.ivan.taskflowapi.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    private ProjectMemberRepository repository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberMapper projectMemberMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private ProjectMemberService projectMemberService;

    @Test
    void changePositionUpdatesRoleOfProjectMember() {
        User owner = user(1L);
        Project project = project(owner);
        ProjectMember member = member(5L, user(2L), ProjectMemberRole.MEMBER);
        ProjectMemberResponseDTO response = new ProjectMemberResponseDTO();
        response.setId(5L);
        response.setRole(ProjectMemberRole.ADMIN);
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(repository.findByIdAndProjectId(5L, 10L)).thenReturn(Optional.of(member));
        when(repository.save(member)).thenReturn(member);
        when(projectMemberMapper.toDTO(member)).thenReturn(response);

        ProjectMemberResponseDTO result = projectMemberService.changePosition(10L, 5L, ProjectMemberRole.ADMIN);

        assertThat(member.getRole()).isEqualTo(ProjectMemberRole.ADMIN);
        assertThat(result).isSameAs(response);
        verify(repository).save(member);
    }

    @Test
    void changePositionRejectsChangingOwnersOwnRole() {
        User owner = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(owner)));
        when(repository.findByIdAndProjectId(5L, 10L))
                .thenReturn(Optional.of(member(5L, owner, ProjectMemberRole.OWNER)));

        assertThatThrownBy(() -> projectMemberService.changePosition(10L, 5L, ProjectMemberRole.ADMIN))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You cannot change your own role");

        verify(repository, never()).save(any());
    }

    @Test
    void changePositionRejectsRolesOtherThanAdminOrMember() {
        User owner = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(owner)));
        when(repository.findByIdAndProjectId(5L, 10L))
                .thenReturn(Optional.of(member(5L, user(2L), ProjectMemberRole.MEMBER)));

        assertThatThrownBy(() -> projectMemberService.changePosition(10L, 5L, ProjectMemberRole.OWNER))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Role must be ADMIN or MEMBER");

        verify(repository, never()).save(any());
    }

    @Test
    void changePositionRequiresProjectOwner() {
        User owner = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(user(3L));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(owner)));

        assertThatThrownBy(() -> projectMemberService.changePosition(10L, 5L, ProjectMemberRole.ADMIN))
                .isInstanceOf(ForbiddenException.class);

        verify(repository, never()).findByIdAndProjectId(any(), any());
    }

    @Test
    void changePositionRejectsNonMemberOfProject() {
        User owner = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(owner)));
        when(repository.findByIdAndProjectId(5L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectMemberService.changePosition(10L, 5L, ProjectMemberRole.ADMIN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Member not found");

        verify(repository, never()).save(any());
    }

    private User user(Long id) {
        return User.builder().id(id).build();
    }

    private Project project(User owner) {
        return Project.builder().id(10L).owner(owner).build();
    }

    private ProjectMember member(Long id, User user, ProjectMemberRole role) {
        return ProjectMember.builder().id(id).user(user).role(role).build();
    }
}
