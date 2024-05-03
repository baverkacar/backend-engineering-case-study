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

    @Scheduled(cron = "0 0 3 * * ?") // 00.00 UTC
    @Transactional
    public void createTournament() {
        log.info("CREATE TOURNAMENT SCHEDULER START");
        Long tournamentId  = tournamentService.createTournament();
        redisService.createTournament(tournamentId);
        redisService.createCountryLeaderBoard(tournamentId);
        log.info("CREATE TOURNAMENT SCHEDULER END");
    }

    @Scheduled(cron = "0 0 23 * * ?") // 20.00 UTC
    @Transactional
    public void closeTournament() {
        log.info("CLOSE TOURNAMENT SCHEDULER START");
        tournamentService.specifyRewardWinners();
        tournamentService.closeTournament();
        redisService.closeTournament();
        log.info("CLOSE TOURNAMENT SCHEDULER END");
    }
}
