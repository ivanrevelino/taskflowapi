package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.invite.InviteResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.ProjectInviteMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectInvite;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.InviteStatus;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.repository.ProjectInviteRepository;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectInviteServiceTest {

    @Mock
    private ProjectInviteRepository inviteRepository;
    @Mock
    private ProjectMemberRepository memberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberService projectMemberService;
    @Mock
    private UserService userService;
    @Mock
    private ProjectInviteMapper inviteMapper;
    @InjectMocks
    private ProjectInviteService projectInviteService;

    @Test
    void inviteSavesPendingInvitationWhenInviterIsAdmin() {
        User inviter = user(1L);
        User invited = user(2L);
        Project project = project(10L, inviter);
        InviteResponseDTO response = response(5L, 10L, 2L, 1L, InviteStatus.PENDING);
        when(userService.getAuthenticatedUser()).thenReturn(inviter);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(memberRepository.findByProjectIdAndUser(10L, inviter))
                .thenReturn(Optional.of(member(ProjectMemberRole.ADMIN)));
        when(userService.findById(2L)).thenReturn(invited);
        when(memberRepository.existsByProjectIdAndUserId(10L, 2L)).thenReturn(false);
        when(inviteRepository.findByProject_IdAndInvited_IdAndStatus(10L, 2L, InviteStatus.PENDING))
                .thenReturn(Optional.empty());
        when(inviteRepository.save(any(ProjectInvite.class))).thenAnswer(invocation -> {
            ProjectInvite saved = invocation.getArgument(0);
            saved.setId(5L);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });
        when(inviteMapper.toDTO(any(ProjectInvite.class))).thenReturn(response);

        InviteResponseDTO result = projectInviteService.invite(10L, 2L, "Welcome");

        assertThat(result).isEqualTo(response);
        verify(inviteRepository).save(argThat(invite ->
                invite.getProject().equals(project)
                        && invite.getInvited().equals(invited)
                        && invite.getInviter().equals(inviter)
                        && invite.getStatus() == InviteStatus.PENDING
                        && invite.getMessage().equals("Welcome")
                        && invite.getExpiresAt().isAfter(LocalDateTime.now().plusDays(6))));
    }

    @Test
    void inviteRejectsMemberWithoutSaving() {
        User inviter = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(inviter);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(10L, inviter)));
        when(memberRepository.findByProjectIdAndUser(10L, inviter))
                .thenReturn(Optional.of(member(ProjectMemberRole.MEMBER)));

        assertThatThrownBy(() -> projectInviteService.invite(10L, 2L, null))
                .isInstanceOf(ForbiddenException.class);
        verify(inviteRepository, never()).save(any());
    }

    @Test
    void inviteRejectsSelfInvitation() {
        User inviter = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(inviter);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(10L, inviter)));
        when(memberRepository.findByProjectIdAndUser(10L, inviter))
                .thenReturn(Optional.of(member(ProjectMemberRole.OWNER)));

        assertThatThrownBy(() -> projectInviteService.invite(10L, 1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You cannot invite yourself");
        verify(userService, never()).findById(any());
    }

    @Test
    void inviteRejectsExistingProjectMember() {
        User inviter = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(inviter);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(10L, inviter)));
        when(memberRepository.findByProjectIdAndUser(10L, inviter))
                .thenReturn(Optional.of(member(ProjectMemberRole.OWNER)));
        when(userService.findById(2L)).thenReturn(user(2L));
        when(memberRepository.existsByProjectIdAndUserId(10L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> projectInviteService.invite(10L, 2L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User is already a member of this project");
        verify(inviteRepository, never()).save(any());
    }

    @Test
    void inviteRejectsExistingUnexpiredPendingInvite() {
        User inviter = user(1L);
        ProjectInvite existing = ProjectInvite.builder()
                .expiresAt(LocalDateTime.now().plusDays(1))
                .status(InviteStatus.PENDING)
                .build();
        setupInviteAsOwner(inviter);
        when(userService.findById(2L)).thenReturn(user(2L));
        when(memberRepository.existsByProjectIdAndUserId(10L, 2L)).thenReturn(false);
        when(inviteRepository.findByProject_IdAndInvited_IdAndStatus(10L, 2L, InviteStatus.PENDING))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> projectInviteService.invite(10L, 2L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User already has a pending invitation");
        verify(inviteRepository, never()).save(any());
    }

    @Test
    void inviteExpiresOldPendingInviteBeforeCreatingNewOne() {
        User inviter = user(1L);
        ProjectInvite existing = ProjectInvite.builder()
                .expiresAt(LocalDateTime.now().minusDays(1))
                .status(InviteStatus.PENDING)
                .build();
        setupInviteAsOwner(inviter);
        when(userService.findById(2L)).thenReturn(user(2L));
        when(memberRepository.existsByProjectIdAndUserId(10L, 2L)).thenReturn(false);
        when(inviteRepository.findByProject_IdAndInvited_IdAndStatus(10L, 2L, InviteStatus.PENDING))
                .thenReturn(Optional.of(existing));
        when(inviteRepository.save(any(ProjectInvite.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inviteMapper.toDTO(any(ProjectInvite.class)))
                .thenReturn(response(6L, 10L, 2L, 1L, InviteStatus.PENDING));

        projectInviteService.invite(10L, 2L, null);

        assertThat(existing.getStatus()).isEqualTo(InviteStatus.EXPIRED);
        verify(inviteRepository, times(2)).save(any(ProjectInvite.class));
    }

    @Test
    void findMyInvitesReturnsPagedInvitesForAuthenticatedUser() {
        User invited = user(2L);
        PageRequest pageable = PageRequest.of(0, 10);
        ProjectInvite invite = ProjectInvite.builder().id(5L).build();
        InviteResponseDTO response = response(5L, 10L, 2L, 1L, InviteStatus.PENDING);
        when(userService.getAuthenticatedUser()).thenReturn(invited);
        when(inviteRepository.findByInvited_Id(2L, pageable))
                .thenReturn(new PageImpl<>(List.of(invite), pageable, 1));
        when(inviteMapper.toDTO(invite)).thenReturn(response);

        Page<InviteResponseDTO> result = projectInviteService.findMyInvites(pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findProjectInvitesRequiresOwnerOrAdminAndPassesStatusFilter() {
        User owner = user(1L);
        PageRequest pageable = PageRequest.of(0, 10);
        Project project = project(10L, owner);
        Page<ProjectInvite> invitePage = new PageImpl<>(List.of(), pageable, 0);
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(memberRepository.findByProjectIdAndUser(10L, owner))
                .thenReturn(Optional.of(member(ProjectMemberRole.OWNER)));
        when(inviteRepository.findByProjectWithStatus(10L, InviteStatus.PENDING, pageable))
                .thenReturn(invitePage);

        Page<InviteResponseDTO> result = projectInviteService.findProjectInvites(10L, InviteStatus.PENDING, pageable);

        assertThat(result).isEmpty();
        verify(inviteRepository).findByProjectWithStatus(10L, InviteStatus.PENDING, pageable);
    }

    @Test
    void findProjectInvitesRejectsRegularMember() {
        User user = user(1L);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(10L, user)));
        when(memberRepository.findByProjectIdAndUser(10L, user))
                .thenReturn(Optional.of(member(ProjectMemberRole.MEMBER)));

        assertThatThrownBy(() -> projectInviteService.findProjectInvites(10L, null, PageRequest.of(0, 10)))
                .isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(inviteRepository);
    }

    @Test
    void acceptAddsUserToProjectAndMarksInviteAccepted() {
        User invited = user(2L);
        User inviter = user(1L);
        Project project = project(10L, inviter);
        ProjectInvite invite = invite(5L, project, invited, inviter, LocalDateTime.now().plusDays(1));
        when(userService.getAuthenticatedUser()).thenReturn(invited);
        when(inviteRepository.findById(5L)).thenReturn(Optional.of(invite));
        when(memberRepository.existsByProjectIdAndUserId(10L, 2L)).thenReturn(false);
        when(inviteRepository.save(invite)).thenReturn(invite);

        projectInviteService.accept(5L);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.ACCEPTED);
        verify(projectMemberService).addInvitedMember(project, invited, inviter);
        verify(inviteRepository).save(invite);
    }

    @Test
    void acceptRejectsInviteOwnedByAnotherUser() {
        ProjectInvite invite = invite(5L, project(10L, user(1L)), user(2L), user(1L), LocalDateTime.now().plusDays(1));
        when(userService.getAuthenticatedUser()).thenReturn(user(3L));
        when(inviteRepository.findById(5L)).thenReturn(Optional.of(invite));

        assertThatThrownBy(() -> projectInviteService.accept(5L))
                .isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(projectMemberService);
        verify(inviteRepository, never()).save(any());
    }

    @Test
    void acceptMarksExpiredPendingInviteExpired() {
        ProjectInvite invite = invite(5L, project(10L, user(1L)), user(2L), user(1L), LocalDateTime.now().minusMinutes(1));
        when(userService.getAuthenticatedUser()).thenReturn(user(2L));
        when(inviteRepository.findById(5L)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);

        assertThatThrownBy(() -> projectInviteService.accept(5L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invitation has expired");

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.EXPIRED);
        verify(inviteRepository).save(invite);
        verifyNoInteractions(projectMemberService);
    }

    @Test
    void acceptRejectsNonPendingInvite() {
        ProjectInvite invite = invite(5L, project(10L, user(1L)), user(2L), user(1L), LocalDateTime.now().plusDays(1));
        invite.setStatus(InviteStatus.REJECTED);
        when(userService.getAuthenticatedUser()).thenReturn(user(2L));
        when(inviteRepository.findById(5L)).thenReturn(Optional.of(invite));

        assertThatThrownBy(() -> projectInviteService.accept(5L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only pending invitations can be accepted");
        verifyNoInteractions(projectMemberService);
        verify(inviteRepository, never()).save(any());
    }

    @Test
    void acceptRejectsUserAlreadyInProject() {
        ProjectInvite invite = invite(5L, project(10L, user(1L)), user(2L), user(1L), LocalDateTime.now().plusDays(1));
        when(userService.getAuthenticatedUser()).thenReturn(user(2L));
        when(inviteRepository.findById(5L)).thenReturn(Optional.of(invite));
        when(memberRepository.existsByProjectIdAndUserId(10L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> projectInviteService.accept(5L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User is already a member of this project");
        verifyNoInteractions(projectMemberService);
        verify(inviteRepository, never()).save(any());
    }

    @Test
    void acceptReturnsNotFoundForUnknownInvite() {
        when(userService.getAuthenticatedUser()).thenReturn(user(2L));
        when(inviteRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectInviteService.accept(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invitation not found");
    }

    private void setupInviteAsOwner(User owner) {
        when(userService.getAuthenticatedUser()).thenReturn(owner);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project(10L, owner)));
        when(memberRepository.findByProjectIdAndUser(10L, owner))
                .thenReturn(Optional.of(member(ProjectMemberRole.OWNER)));
    }

    private User user(Long id) {
        return User.builder().id(id).build();
    }

    private Project project(Long id, User owner) {
        return Project.builder().id(id).owner(owner).build();
    }

    private ProjectMember member(ProjectMemberRole role) {
        return ProjectMember.builder().role(role).build();
    }

    private ProjectInvite invite(Long id, Project project, User invited, User inviter, LocalDateTime expiresAt) {
        return ProjectInvite.builder()
                .id(id)
                .project(project)
                .invited(invited)
                .inviter(inviter)
                .expiresAt(expiresAt)
                .status(InviteStatus.PENDING)
                .build();
    }

    private InviteResponseDTO response(Long id, Long projectId, Long invitedId, Long inviterId, InviteStatus status) {
        return new InviteResponseDTO(id, projectId, invitedId, inviterId, LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), status, null);
    }
}
