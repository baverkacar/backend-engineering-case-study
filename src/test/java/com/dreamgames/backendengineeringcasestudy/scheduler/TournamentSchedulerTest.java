package com.dreamgames.backendengineeringcasestudy.scheduler;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.dreamgames.backendengineeringcasestudy.domain.Tournament;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class TournamentSchedulerTest {

    @InjectMocks
    private TournamentScheduler scheduler;
    @Mock
    private TournamentRepository tournamentRepository;


    @Test
    public void testCreateTournament() {

        scheduler.createTournament();
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    public void testCloseTournament() {
        Tournament activeTournament = new Tournament();
        activeTournament.setTournamentId(1L);
        activeTournament.setStatus("Active");

        when(tournamentRepository.findFirstByStatus("Active")).thenReturn(activeTournament);

        scheduler.closeTournament();

        verify(tournamentRepository, times(1)).save(activeTournament);
        assertEquals("Completed", activeTournament.getStatus());
    }
}