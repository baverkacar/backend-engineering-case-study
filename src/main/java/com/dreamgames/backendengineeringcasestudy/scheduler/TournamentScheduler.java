package com.dreamgames.backendengineeringcasestudy.scheduler;

import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentScheduler {
    private final TournamentService tournamentService;
    private final RedisService redisService;

    @Scheduled(cron = "0 0 0 * * ?") // Midnight
    @Transactional
    public void createTournament() {
        Long tournamentId  = tournamentService.createTournament();

        redisService.createTournament(tournamentId);
        redisService.createCountryLeaderBoard(tournamentId);

    }

    @Scheduled(cron = "0 0 20 * * ?") // 8pm
    @Transactional
    public void closeTournament() {
        tournamentService.closeTournament();
        redisService.closeTournament();
        // TODO: Buraya reward logic'ini ekle. Async olacak.
    }
}
