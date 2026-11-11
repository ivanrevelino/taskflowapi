package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.models.enums.LeaderboardPeriod;
import com.ivan.taskflowapi.repository.LeaderboardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void weeklyPeriodShouldQueryFromMondayThroughNextMonday() {
        when(leaderboardRepository.findProjectLeaderboard(anyLong(), anyBoolean(), any(), any())).thenReturn(List.of());
        LocalDate start = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        assertThat(leaderboardService.getLeaderboard(1L, LeaderboardPeriod.WEEKLY)).isEmpty();

        verify(leaderboardRepository).findProjectLeaderboard(1L, false, start.atStartOfDay(), start.plusWeeks(1).atStartOfDay());
    }

    @Test
    void monthlyPeriodShouldQueryFromMonthStartThroughNextMonth() {
        when(leaderboardRepository.findProjectLeaderboard(anyLong(), anyBoolean(), any(), any())).thenReturn(List.of());
        LocalDate start = LocalDate.now().withDayOfMonth(1);

        assertThat(leaderboardService.getLeaderboard(1L, LeaderboardPeriod.MONTHLY)).isEmpty();

        verify(leaderboardRepository).findProjectLeaderboard(1L, false, start.atStartOfDay(), start.plusMonths(1).atStartOfDay());
    }

    @Test
    void allTimePeriodShouldNotApplyDateFilters() {
        when(leaderboardRepository.findProjectLeaderboard(eq(1L), eq(true), any(), any())).thenReturn(List.of());

        assertThat(leaderboardService.getLeaderboard(1L, LeaderboardPeriod.ALL_TIME)).isEmpty();

        verify(leaderboardRepository).findProjectLeaderboard(eq(1L), eq(true), any(), any());
    }

    @Test
    void shouldRejectNonPositiveProjectId() {
        assertThatThrownBy(() -> leaderboardService.getLeaderboard(0L, LeaderboardPeriod.MONTHLY))
                .isInstanceOf(BadRequestException.class);

        verifyNoInteractions(projectService, leaderboardRepository);
    }

    @Test
    void shouldNotQueryLeaderboardWhenProjectAccessIsDenied() {
        doThrow(new ForbiddenException("Not a project member")).when(projectService).findById(1L);

        assertThatThrownBy(() -> leaderboardService.getLeaderboard(1L, LeaderboardPeriod.MONTHLY))
                .isInstanceOf(ForbiddenException.class);

        verifyNoInteractions(leaderboardRepository);
    }
}
