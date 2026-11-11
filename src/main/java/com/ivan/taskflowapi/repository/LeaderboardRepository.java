package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.dto.leaderboard.LeaderboardEntryDTO;
import com.ivan.taskflowapi.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Project, Long> {

    @Query(value = """
            SELECT ranked.user_id,
                   ranked.username,
                   ranked.completed_tasks,
                   CAST(DENSE_RANK() OVER (ORDER BY ranked.completed_tasks DESC) AS INTEGER)
            FROM (
                SELECT users.id AS user_id,
                       users.username AS username,
                       COUNT(tasks.id) AS completed_tasks
                FROM (
                    SELECT DISTINCT user_id
                    FROM project_members_tb
                    WHERE project_id = :projectId
                ) members
                JOIN users_tb users ON users.id = members.user_id
                LEFT JOIN tasks_tb tasks
                    ON tasks.project_id = :projectId
                    AND tasks.assignee_id = users.id
                    AND tasks.status = 'COMPLETED'
                    AND (:allTime = TRUE OR (tasks.completed_at >= :startAt AND tasks.completed_at < :endAt))
                GROUP BY users.id, users.username
            ) ranked
            ORDER BY ranked.completed_tasks DESC, ranked.username ASC, ranked.user_id ASC
            """, nativeQuery = true)
    List<LeaderboardEntryDTO> findProjectLeaderboard(
            @Param("projectId") Long projectId,
            @Param("allTime") boolean allTime,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);
}
