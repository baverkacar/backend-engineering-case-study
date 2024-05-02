package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import com.dreamgames.backendengineeringcasestudy.exception.UserDidNotEnteredTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.CountryLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.repository.GroupInfoRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private final GroupInfoRepository groupInfoRepository;
    private final RedisService redisService;
    private final UserRepository userRepository;

    /**
     * Retrieves the leaderboard for a specific group by group ID. This method fetches the leaderboard data from Redis
     * and maps it to a list of GroupLeaderBoard objects containing user details and scores.
     * Each user's information is fetched from the groupInfo repository.
     *
     * @param groupId The ID of the group for which to retrieve the leaderboard.
     * @return A list of GroupLeaderBoard objects representing the current state of the leaderboard for the specified group.
     * @throws IllegalStateException If user information corresponding to any userId in the leaderboard cannot be found.
     */
    @Override
    public List<GroupLeaderBoard> getGroupLeaderBoardWithGroupId(Long groupId) {
        Set<ZSetOperations.TypedTuple<String>> groupLeaderBoard = redisService.getGroupLeaderBoard(groupId);
        return groupLeaderBoard.stream().map(scoreEntry -> {
            String member = scoreEntry.getValue();
            Long userId = Long.parseLong(member.split(":")[1]);
            Integer score = scoreEntry.getScore().intValue();

            GroupInfo groupInfo = groupInfoRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("User info not found for userId: " + userId));

            return new GroupLeaderBoard(
                    userId,
                    groupInfo.getUser().getUsername(),
                    groupInfo.getUser().getCountry(),
                    score
            );
        }).collect(Collectors.toList());
    }


    /**
     * Retrieves the leaderboard for a user by their userId. This method finds the tournament the user is participating in,
     * fetches the corresponding group, and then retrieves the leaderboard for that group.
     *
     * @param userId The ID of the user whose tournament group leaderboard is to be retrieved.
     * @return A list of GroupLeaderBoard objects representing the leaderboard of the group in which the user is a participant.
     * @throws UserNotFoundException If no user is found with the provided userId.
     * @throws UserDidNotEnteredTournamentException If the user has not entered any active tournament.
     */
    @Override
    public List<GroupLeaderBoard> getGroupLeaderBoardWithUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user found with id: " + userId));

        Long tournamentId = redisService.getActiveTournamentId();
        GroupInfo groupInfo = groupInfoRepository.findByTournamentIdAndUserId(tournamentId, userId).orElseThrow(
                () -> new UserDidNotEnteredTournamentException("User did not entered any active tournament " + tournamentId)
        );

        return getGroupLeaderBoardWithGroupId(groupInfo.getGroup().getGroupId());
    }


    /**
     * Retrieves the country leaderboard for the current active tournament.
     * This method first checks if there is an active tournament and then fetches the country leaderboard from Redis.
     * The leaderboard includes the country names and their corresponding scores sorted in descending order.
     *
     * @return A list of CountryLeaderBoard objects representing the current state of the leaderboard for the current tournament.
     * @throws IllegalStateException If there is no active tournament at the time of the request.
     */
    @Override
    public List<CountryLeaderBoard> getCountryLeaderBoardCurrentTournament() {

        redisService.checkActiveTournament();
        Long tournamentId = redisService.getActiveTournamentId();
        String leaderboardKey = "countryLeaderBoard:" + tournamentId;
        Set<ZSetOperations.TypedTuple<String>> rawScores = redisService.getCountryLeaderBoard(leaderboardKey);

        if (rawScores == null || rawScores.isEmpty()) {
            return Collections.emptyList();
        }

        return rawScores.stream()
                .map(scoreEntry -> {
                    String countryName = scoreEntry.getValue();
                    Integer score = scoreEntry.getScore() != null ? scoreEntry.getScore().intValue() : 0;
                    return new CountryLeaderBoard(countryName, score);
                })
                .collect(Collectors.toList());
    }


    /**
     * Retrieves the rank of a user within their tournament group.
     * This method checks if the user is registered in a group for the specified tournament and then queries Redis
     * to find the user's rank within that group's leaderboard.
     *
     * @param tournamentId The ID of the tournament for which to retrieve the user's group rank.
     * @param userId The ID of the user whose rank is to be determined.
     * @return The rank of the user within their group as an integer.
     * @throws UserDidNotEnteredTournamentException If the user is not registered in any group for the specified tournament.
     */
    @Override
    public Integer getUserTournamentGroupRank(Long tournamentId, Long userId) {
        GroupInfo groupInfo = groupInfoRepository.findByTournamentIdAndUserId(tournamentId, userId)
                .orElseThrow(() -> new UserDidNotEnteredTournamentException("User is not registered in any group for this tournament."));

        return redisService.getRankOfUserInGroupLeaderBoard(groupInfo.getGroup().getGroupId(), userId);
    }
}
