package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.ProjectInvite;
import com.ivan.taskflowapi.models.enums.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Long> {

    Optional<ProjectInvite> findByProject_IdAndInvited_IdAndStatus(
            Long projectId, Long invitedId, InviteStatus status);

    Page<ProjectInvite> findByInvited_Id(Long invitedId, Pageable pageable);

    @Query("""
            SELECT invite
            FROM ProjectInvite invite
            WHERE invite.project.id = :projectId
              AND (:status IS NULL OR invite.status = :status)
            """)
    Page<ProjectInvite> findByProjectWithStatus(
            @Param("projectId") Long projectId,
            @Param("status") InviteStatus status,
            Pageable pageable);
}
