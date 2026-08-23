package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    Optional<ProjectMember> findByIdAndProjectId(Long memberId, Long projectId);
}
