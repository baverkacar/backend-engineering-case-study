package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.leaderboard.CountryLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;

import java.util.ArrayList;
import java.util.List;

public interface LeaderBoardService {
    List<GroupLeaderBoard> getGroupLeaderBoardWithGroupId(Long groupId);
    List<GroupLeaderBoard> getGroupLeaderBoardWithUserId(Long userId);
    List<CountryLeaderBoard> getCountryLeaderBoardCurrentTournament();
    Integer getUserTournamentGroupRank(Long tournamentId, Long userId);
}
