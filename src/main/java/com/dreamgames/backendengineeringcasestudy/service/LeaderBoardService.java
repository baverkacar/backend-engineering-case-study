package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;

import java.util.List;

public interface LeaderBoardService {
    List<GroupLeaderBoard> getGroupLeaderBoard(Long groupId);
}
