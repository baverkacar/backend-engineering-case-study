package com.dreamgames.backendengineeringcasestudy.scheduler;

import static org.mockito.Mockito.*;

import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class TournamentSchedulerTest {

    @InjectMocks
    private TournamentScheduler scheduler;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private RedisService redisService;


    @Test
    public void testCreateTournamentScheduler() {
        // Arrange
        Long expectedTournamentId = 1L;
        when(tournamentService.createTournament()).thenReturn(expectedTournamentId);

        // Act
        scheduler.createTournament();

        // Assert
        verify(tournamentService, times(1)).createTournament();
        verify(redisService, times(1)).createTournament(expectedTournamentId);
        verify(redisService, times(1)).createCountryLeaderBoard(expectedTournamentId);
        verifyNoMoreInteractions(tournamentService, redisService);
    }

    @Test
    public void testCloseTournamentScheduler() {
        // Act
        scheduler.closeTournament();

        // Assert
        verify(tournamentService, times(1)).specifyRewardWinners();
        verify(tournamentService, times(1)).closeTournament();
        verify(redisService, times(1)).closeTournament();
        verifyNoMoreInteractions(tournamentService, redisService);
    }
}