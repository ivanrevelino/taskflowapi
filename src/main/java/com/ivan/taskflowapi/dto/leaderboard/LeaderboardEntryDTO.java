package com.ivan.taskflowapi.dto.leaderboard;

public record LeaderboardEntryDTO(
        Long userId,
        String username,
        Long completedTasks,
        Integer position
) {
}
