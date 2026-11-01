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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectInviteService {

    private static final int INVITE_VALIDITY_DAYS = 7;

    private final ProjectInviteRepository inviteRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberService projectMemberService;
    private final UserService userService;
    private final ProjectInviteMapper inviteMapper;

    public Page<InviteResponseDTO> findMyInvites(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        return inviteRepository.findByInvited_Id(user.getId(), pageable).map(inviteMapper::toDTO);
    }

    public Page<InviteResponseDTO> findProjectInvites(
            Long projectId, InviteStatus status, Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        ProjectMember membership = memberRepository.findByProjectIdAndUser(projectId, user)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this project"));

        if (membership.getRole() != ProjectMemberRole.OWNER
                && membership.getRole() != ProjectMemberRole.ADMIN) {
            throw new ForbiddenException("Only project owners and administrators can list invitations");
        }

        return inviteRepository.findByProjectWithStatus(project.getId(), status, pageable)
                .map(inviteMapper::toDTO);
    }

    public InviteResponseDTO invite(Long projectId, Long invitedId, String message) {
        User inviter = userService.getAuthenticatedUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        ProjectMember inviterMembership = memberRepository.findByProjectIdAndUser(projectId, inviter)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this project"));

        if (inviterMembership.getRole() != ProjectMemberRole.OWNER
                && inviterMembership.getRole() != ProjectMemberRole.ADMIN) {
            throw new ForbiddenException("Only project owners and administrators can invite users");
        }

        if (inviter.getId().equals(invitedId)) {
            throw new BadRequestException("You cannot invite yourself");
        }

        User invited = userService.findById(invitedId);

        if (memberRepository.existsByProjectIdAndUserId(projectId, invitedId)) {
            throw new BadRequestException("User is already a member of this project");
        }

        inviteRepository.findByProject_IdAndInvited_IdAndStatus(projectId, invitedId, InviteStatus.PENDING)
                .ifPresent(existing -> {
                    if (existing.getExpiresAt().isAfter(LocalDateTime.now())) {
                        throw new BadRequestException("User already has a pending invitation");
                    }
                    existing.setStatus(InviteStatus.EXPIRED);
                    inviteRepository.save(existing);
                });

        ProjectInvite invite = ProjectInvite.builder()
                .project(project)
                .invited(invited)
                .inviter(inviter)
                .expiresAt(LocalDateTime.now().plusDays(INVITE_VALIDITY_DAYS))
                .status(InviteStatus.PENDING)
                .message(message)
                .build();

        return inviteMapper.toDTO(inviteRepository.save(invite));
    }

    @Transactional(noRollbackFor = BadRequestException.class)
    public void accept(Long inviteId) {
        User user = userService.getAuthenticatedUser();
        ProjectInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invite.getInvited().getId().equals(user.getId())) {
            throw new ForbiddenException("This invitation does not belong to you");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new BadRequestException("Only pending invitations can be accepted");
        }

        if (!invite.getExpiresAt().isAfter(LocalDateTime.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            inviteRepository.save(invite);
            throw new BadRequestException("Invitation has expired");
        }

        if (memberRepository.existsByProjectIdAndUserId(invite.getProject().getId(), user.getId())) {
            throw new BadRequestException("User is already a member of this project");
        }

        projectMemberService.addInvitedMember(invite.getProject(), user, invite.getInviter());
        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);
    }

}
