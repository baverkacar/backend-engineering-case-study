package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.leaderboard.CountryLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final RedisService redisService;
    private final LeaderBoardService leaderBoardService;


    /**
     * Allows a user to enter an active tournament and returns the current group leaderboard.
     * This method checks the user's eligibility, assigns them to a tournament group, and retrieves the group's leaderboard.
     *
     * @param userId the ID of the user who is attempting to enter the tournament.
     * @return a ResponseEntity containing a list of {@link GroupLeaderBoard} detailing the current group leaderboard and HTTP status code.
     *         Returns HttpStatus.OK if the user successfully enters the tournament.
     *         Returns an error status if the user cannot enter the tournament due to various eligibility checks.
     */
    @PostMapping("/enter/{userId}")
    public ResponseEntity<List<GroupLeaderBoard>> enterTournament(@PathVariable Long userId) {
        List<GroupLeaderBoard> leaderBoard =  tournamentService.enterTournament(userId);
        return new ResponseEntity<>(leaderBoard, HttpStatus.OK);
    }

    /**
     * Retrieves the leaderboard for a specific user's tournament group.
     * This method finds the group to which the user belongs and returns the leaderboard of that group.
     *
     * @param userId The ID of the user whose group leaderboard is requested.
     * @return ResponseEntity containing a list of GroupLeaderBoard entries and HTTP status code.
     */
    @GetMapping("/group-leaderboard/{userId}")
    public ResponseEntity<List<GroupLeaderBoard>> getGroupLeaderBoard(@PathVariable Long userId) {
        List<GroupLeaderBoard> leaderBoard = leaderBoardService.getGroupLeaderBoardWithUserId(userId);
        return new ResponseEntity<>(leaderBoard, HttpStatus.OK);
    }

    /**
     * Retrieves the leaderboard for all countries in the current tournament.
     * This method returns the country scores for the ongoing tournament, showing each country's cumulative score.
     *
     * @return ResponseEntity containing a list of CountryLeaderBoard entries and HTTP status code.
     */
    @GetMapping("/country-leaderboard")
    public ResponseEntity<List<CountryLeaderBoard>> getCountryLeaderBoard() {
        List<CountryLeaderBoard> leaderBoard = leaderBoardService.getCountryLeaderBoardCurrentTournament();
        return new ResponseEntity<>(leaderBoard, HttpStatus.OK);
    }

    /**
     * Retrieves the ranking of a user within their tournament group.
     * This method determines the user's rank based on their score within the group they are participating in for the specified tournament.
     *
     * @param tournamentId The ID of the tournament in which the user is participating.
     * @param userId The ID of the user whose rank is being queried.
     * @return ResponseEntity containing the user's rank and HTTP status code.
     */
    @GetMapping("/rank/{tournamentId}/{userId}")
    public ResponseEntity<Integer> getUserTournamentGroupRank(@PathVariable Long tournamentId, @PathVariable Long userId) {
        Integer rank = leaderBoardService.getUserTournamentGroupRank(tournamentId, userId);
        return new ResponseEntity<>(rank, HttpStatus.OK);
    }

    /**
     * Claims all unclaimed tournament rewards for a specific user.
     * This endpoint triggers the reward claiming process which calculates the total unclaimed coins won by the user
     * in tournaments and updates their coin balance accordingly.
     *
     * @param userId the ID of the user whose rewards are to be claimed.
     * @return HTTP status indicating the outcome of the operation.
     *         Returns HttpStatus.OK if the operation is successful.
     *         Returns an error status if the operation fails, e.g., if there are no unclaimed rewards or if the user does not exist.
     */
    @PostMapping("claim-rewards/{userId}")
    public HttpStatus claimTournamentsReward(@PathVariable Long userId) {
        tournamentService.claimTournamentsReward(userId);
        return HttpStatus.OK;
    }


    // JUST FOR TESTING
    @PostMapping("/test/create")
    public HttpStatus createTournament() {
        Long tournamentId  = tournamentService.createTournament();
        redisService.createTournament(tournamentId);
        redisService.createCountryLeaderBoard(tournamentId);
        return HttpStatus.OK;
    }

    @PostMapping("/test/close")
    public HttpStatus closeTournament() {
        tournamentService.specifyRewardWinners();
        tournamentService.closeTournament();
        redisService.closeTournament();
        return HttpStatus.OK;
    }
}
