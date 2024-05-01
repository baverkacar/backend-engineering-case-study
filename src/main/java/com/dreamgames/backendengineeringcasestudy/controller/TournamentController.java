package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final RedisService redisService;

    @PostMapping("/enter/{userId}")
    public ResponseEntity<List<GroupLeaderBoard>> enterTournament(@PathVariable Long userId) {
        List<GroupLeaderBoard> leaderBoard =  tournamentService.enterTournament(userId);
        return new ResponseEntity<>(leaderBoard, HttpStatus.OK);
    }

    @PostMapping("/create")
    public HttpStatus createTournament() {
        Long tournamentId  = tournamentService.createTournament();

        redisService.createTournament(tournamentId);
        redisService.createCountryLeaderBoard(tournamentId);
        return HttpStatus.OK;
    }
}
