package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    Optional<ProjectMember> findByIdAndProjectId(Long memberId, Long projectId);

    Page<ProjectMember> findByUser(User user, Pageable pageable);

    Optional<ProjectMember> findByProjectIdAndUser(Long projectId, User user);

    List<ProjectMember> findByProjectId(Long projectId);
}
