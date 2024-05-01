package com.dreamgames.backendengineeringcasestudy.service;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface RedisService {
    void createTournament(Long tournamentId);
    void closeTournament();
    void checkActiveTournament();
    Long getActiveTournamentId();
    void createCountryLeaderBoard(Long tournamentId);
    void updateGroupLeaderBoard(Long groupId, Long userId, Integer score);
    void initializeGroup(Long groupId);
    Set<ZSetOperations.TypedTuple<String>> getGroupLeaderBoard(Long groupId);
    void addCountryToGroup(String groupCountryKey, String country);
    boolean canUserJoinGroup(Long groupId, String country);
    void incrementCountryLeaderBoardScore(String country, int scoreIncrement, Long tournamentId);
    void incrementGroupLeaderBoardScore(Long groupId, Long id, int i);
}
