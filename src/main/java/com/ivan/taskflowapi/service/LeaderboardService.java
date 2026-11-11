package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.leaderboard.LeaderboardEntryDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.models.enums.LeaderboardPeriod;
import com.ivan.taskflowapi.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final ProjectService projectService;

    public List<LeaderboardEntryDTO> getLeaderboard(Long projectId, LeaderboardPeriod period) {
        if (projectId == null || projectId <= 0) {
            throw new BadRequestException("Invalid project ID");
        }

        projectService.findById(projectId);

        if (period == LeaderboardPeriod.ALL_TIME) {
            LocalDateTime now = LocalDateTime.now();
            return leaderboardRepository.findProjectLeaderboard(projectId, true, now, now);
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;
        if (period == LeaderboardPeriod.WEEKLY) {
            startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            endDate = startDate.plusWeeks(1);
        } else {
            startDate = today.withDayOfMonth(1);
            endDate = startDate.plusMonths(1);
        }

        return leaderboardRepository.findProjectLeaderboard(
                projectId,
                false,
                startDate.atStartOfDay(),
                endDate.atStartOfDay());
    }
}
