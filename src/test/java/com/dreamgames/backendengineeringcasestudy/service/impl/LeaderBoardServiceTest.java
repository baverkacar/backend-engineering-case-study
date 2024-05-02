package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import com.dreamgames.backendengineeringcasestudy.domain.TournamentGroups;
import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.UserDidNotEnteredTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
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
    void whenValidUserId_thenRetrieveLeaderBoard() {
        // Arrange
        Long userId = 1L;
        Long tournamentId = 2L;
        Long groupId = 3L;
        User user = new User(); // Assume User is correctly instantiated
        GroupInfo groupInfo = new GroupInfo(); // Assume GroupInfo is correctly instantiated

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(redisService.getActiveTournamentId()).thenReturn(tournamentId);
        when(groupInfoRepository.findByTournamentIdAndUserId(tournamentId, userId)).thenReturn(Optional.of(groupInfo));
        when(leaderBoardService.getGroupLeaderBoardWithGroupId(groupId)).thenReturn(Collections.emptyList());

        // Act
        List<GroupLeaderBoard> result = leaderBoardService.getGroupLeaderBoardWithUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals("username", result.get(0).getUsername());
    }

    @Test
    void whenInvalidUserId_thenThrowUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            leaderBoardService.getGroupLeaderBoardWithUserId(userId);
        });
    }

    @Test
    void whenUserNotEnteredTournament_thenThrowException() {
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
}