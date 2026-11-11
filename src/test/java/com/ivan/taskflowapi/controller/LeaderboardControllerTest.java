package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.leaderboard.LeaderboardEntryDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.models.enums.LeaderboardPeriod;
import com.ivan.taskflowapi.security.filters.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaderboardService leaderboardService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void shouldReturnLeaderboardWithPosition() throws Exception {
        Mockito.when(leaderboardService.getLeaderboard(1L, LeaderboardPeriod.MONTHLY))
                .thenReturn(List.of(
                        new LeaderboardEntryDTO(2L, "member02", 4L, 1),
                        new LeaderboardEntryDTO(1L, "member01", 4L, 1),
                        new LeaderboardEntryDTO(3L, "member03", 0L, 2)));

        mockMvc.perform(get("/api/v1/projects/1/leaderboard?period=MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].completedTasks").value(4))
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[1].position").value(1))
                .andExpect(jsonPath("$[2].completedTasks").value(0))
                .andExpect(jsonPath("$[2].position").value(2));
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotProjectMember() throws Exception {
        Mockito.when(leaderboardService.getLeaderboard(1L, LeaderboardPeriod.MONTHLY))
                .thenThrow(new ForbiddenException("You don't make part of this project"));

        mockMvc.perform(get("/api/v1/projects/1/leaderboard?period=MONTHLY"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenProjectDoesNotExist() throws Exception {
        Mockito.when(leaderboardService.getLeaderboard(999L, LeaderboardPeriod.MONTHLY))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(get("/api/v1/projects/999/leaderboard?period=MONTHLY"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidProjectId() throws Exception {
        Mockito.when(leaderboardService.getLeaderboard(0L, LeaderboardPeriod.MONTHLY))
                .thenThrow(new BadRequestException("Invalid project ID"));

        mockMvc.perform(get("/api/v1/projects/0/leaderboard?period=MONTHLY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidPeriod() throws Exception {
        mockMvc.perform(get("/api/v1/projects/1/leaderboard?period=YEARLY"))
                .andExpect(status().isBadRequest());
    }
}
