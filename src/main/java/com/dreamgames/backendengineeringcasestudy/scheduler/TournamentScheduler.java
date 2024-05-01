package com.dreamgames.backendengineeringcasestudy.scheduler;

import com.dreamgames.backendengineeringcasestudy.domain.Tournament;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TournamentScheduler {
    private final TournamentRepository tournamentRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Midnight
    @Transactional
    public void createTournament() {
        Tournament newTournament = new Tournament();
        newTournament.setStartTime(LocalDateTime.now());
        newTournament.setEndTime(LocalDateTime.now().plusHours(20));
        newTournament.setStatus("Active");
        tournamentRepository.save(newTournament);
    }

    @Scheduled(cron = "0 0 20 * * ?") // 8pm
    @Transactional
    public void closeTournament() {
        Tournament activeTournament = tournamentRepository.findFirstByStatus("Active");
        if (activeTournament != null) {
            activeTournament.setStatus("Completed");
            tournamentRepository.save(activeTournament);
        }
    }
}
