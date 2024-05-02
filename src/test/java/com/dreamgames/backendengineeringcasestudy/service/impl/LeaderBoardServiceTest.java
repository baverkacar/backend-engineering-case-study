package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import com.dreamgames.backendengineeringcasestudy.domain.TournamentGroups;
import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.UserDidNotEnteredTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.CountryLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.repository.GroupInfoRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeaderBoardServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private GroupInfoRepository groupInfoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaderBoardServiceImpl leaderBoardService;


    @Test
    void whenGetGroupLeaderBoardWithGroupId_thenReturnSortedLeaderBoard() {
        // Arrange
        Long groupId = 1L;
        Long userId = 100L;
        String redisKey = "groupLeaderBoard:" + groupId;
        Set<ZSetOperations.TypedTuple<String>> redisData = new HashSet<>();
        ZSetOperations.TypedTuple<String> typedTuple = new DefaultTypedTuple<>("user:" + userId, 10.0);
        redisData.add(typedTuple);

        when(redisService.getGroupLeaderBoard(groupId)).thenReturn(redisData);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testUser");
        mockUser.setCountry("USA");

        GroupInfo mockGroupInfo = new GroupInfo();
        mockGroupInfo.setUser(mockUser);

        when(groupInfoRepository.findByUserId(userId)).thenReturn(Optional.of(mockGroupInfo));

        // Act
        List<GroupLeaderBoard> result = leaderBoardService.getGroupLeaderBoardWithGroupId(groupId);

        // Assert
        assertEquals(1, result.size());
        GroupLeaderBoard leaderBoardEntry = result.get(0);
        assertEquals(userId, leaderBoardEntry.getUserId());
        assertEquals("testUser", leaderBoardEntry.getUsername());
        assertEquals("USA", leaderBoardEntry.getCountry());
        assertEquals(10, leaderBoardEntry.getTournamentScore());
    }

    @Test
    void getGroupLeaderBoardWithUserId_whenInvalidUserId_thenThrowUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            leaderBoardService.getGroupLeaderBoardWithUserId(userId);
        });
    }

    @Test
    void getGroupLeaderBoardWithUserId_whenUserNotEnteredTournament_thenThrowException() {
        // Arrange
        Long userId = 1L;
        Long tournamentId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(redisService.getActiveTournamentId()).thenReturn(tournamentId);
        when(groupInfoRepository.findByTournamentIdAndUserId(tournamentId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserDidNotEnteredTournamentException.class, () -> {
            leaderBoardService.getGroupLeaderBoardWithUserId(userId);
        });
    }

    @Test
    void getCountryLeaderBoardCurrentTournament_Success() {
        // Arrange
        Long tournamentId = 1L;
        String leaderboardKey = "countryLeaderBoard:" + tournamentId;
        Set<ZSetOperations.TypedTuple<String>> mockLeaderBoard = new HashSet<>();
        ZSetOperations.TypedTuple<String> country1 = new DefaultTypedTuple<>("Country1", 100.0);
        ZSetOperations.TypedTuple<String> country2 = new DefaultTypedTuple<>("Country2", 80.0);
        mockLeaderBoard.add(country1);
        mockLeaderBoard.add(country2);

        when(redisService.getActiveTournamentId()).thenReturn(tournamentId);
        when(redisService.getCountryLeaderBoard(leaderboardKey)).thenReturn(mockLeaderBoard);

        // Act
        List<CountryLeaderBoard> result = leaderBoardService.getCountryLeaderBoardCurrentTournament();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Country2", result.get(0).getCountryName());
        assertEquals(80, result.get(0).getScore());
        assertEquals("Country1", result.get(1).getCountryName());
        assertEquals(100, result.get(1).getScore());
    }

    @Test
    void getCountryLeaderBoardCurrentTournament_EmptyLeaderboard() {
        // Arrange
        Long tournamentId = 1L;
        String leaderboardKey = "countryLeaderBoard:" + tournamentId;
        when(redisService.getActiveTournamentId()).thenReturn(tournamentId);
        when(redisService.getCountryLeaderBoard(leaderboardKey)).thenReturn(new HashSet<>());

        // Act
        List<CountryLeaderBoard> result = leaderBoardService.getCountryLeaderBoardCurrentTournament();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserTournamentGroupRank_Success() {
        // Arrange
        Long tournamentId = 1L;
        Long userId = 1L;
        Long groupId = 10L;
        Integer expectedRank = 1;

        GroupInfo mockGroupInfo = new GroupInfo();

        mockGroupInfo.setGroup(TournamentGroups.builder().groupId(groupId).build());

        when(groupInfoRepository.findByTournamentIdAndUserId(tournamentId, userId))
                .thenReturn(Optional.of(mockGroupInfo));
        when(redisService.getRankOfUserInGroupLeaderBoard(groupId, userId)).thenReturn(expectedRank);

        // Act
        Integer actualRank = leaderBoardService.getUserTournamentGroupRank(tournamentId, userId);

        // Assert
        assertEquals(expectedRank, actualRank);
    }
}