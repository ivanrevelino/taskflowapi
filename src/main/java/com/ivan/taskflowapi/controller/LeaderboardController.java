package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.leaderboard.LeaderboardEntryDTO;
import com.ivan.taskflowapi.models.enums.LeaderboardPeriod;
import com.ivan.taskflowapi.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public List<LeaderboardEntryDTO> getLeaderboard(
            @PathVariable Long projectId,
            @RequestParam LeaderboardPeriod period) {
        return leaderboardService.getLeaderboard(projectId, period);
    }
}
