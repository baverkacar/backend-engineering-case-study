package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;

import java.util.List;

public interface TournamentService {
    List<GroupLeaderBoard> enterTournament(Long userId);
    Long createTournament();
    void closeTournament();
    void specifyRewardWinners();
    void claimTournamentsReward(Long userId);
}
