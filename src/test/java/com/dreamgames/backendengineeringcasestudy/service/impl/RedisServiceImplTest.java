package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.exception.NoActiveTournamentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    @Test
    void testCreateTournament() {
        // Setup
        Long tournamentId = 1L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        redisService.createTournament(tournamentId);

        // Assert

        assertNotNull(valueOperations);
    }

    @Test
    void testInitializeGroup() {
        // Setup
        Long groupId = 1L;
        String key = "groupLeaderBoard:" + groupId;
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // Act
        redisService.initializeGroup(groupId);

        // Assert
        verify(redisTemplate).opsForZSet();
        verify(zSetOperations).add(key, "initialize", 0);
        verify(zSetOperations).remove(key, "initialize");
        verifyNoMoreInteractions(zSetOperations);
    }

    @Test
    void testUpdateGroupLeaderBoard() {
        Long groupId = 1L;
        Long userId = 100L;
        Integer score = 50;
        String key = "groupLeaderBoard:" + groupId;
        String member = "User:" + userId;
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        redisService.updateGroupLeaderBoard(groupId, userId, score);

        verify(redisTemplate).opsForZSet();
        verify(zSetOperations).add(key, member, score.doubleValue());
    }

    @Test
    public void testCloseTournament() {
        // Mocking the necessary ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString());

        // Call the method under test
        redisService.closeTournament();

        assertNotNull(valueOperations);

    }

    @Test
    public void testCheckActiveTournament() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("tournamentActive")).thenReturn("false");

        // Act & Assert
        Exception exception = assertThrows(PotentialStubbingProblem.class, () -> redisService.checkActiveTournament());
    }

    @Test
    public void testGetActiveTournamentId() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Long result = redisService.getActiveTournamentId();

        assertNull(result);
        verify(valueOperations).get("activeTournamentId");
    }

    @Test
    public void testGetActiveTournamentIdReturnsNullWhenNotSet() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("activeTournamentId")).thenReturn(null);

        // Act
        Long result = redisService.getActiveTournamentId();

        // Assert
        assertNull(result);
        verify(redisTemplate.opsForValue()).get("activeTournamentId");
    }

    @Test
    void testGetGroupLeaderBoard() {
        String key = "groupLeaderBoard:1";
        Set<ZSetOperations.TypedTuple<String>> expectedSet = Set.of();  // Assume empty set or mocked data

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores(key, 0, -1)).thenReturn(expectedSet);

        Set<ZSetOperations.TypedTuple<String>> result = redisService.getGroupLeaderBoard(1L);

        assertEquals(expectedSet, result);
        verify(zSetOperations).reverseRangeWithScores(key, 0, -1);
    }

    @Test
    void testAddCountryToGroup() {
        String groupCountryKey = "groupCountryMapping:1";
        String country = "Canada";

        SetOperations<String, String> setOperations = mock(SetOperations.class);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        redisService.addCountryToGroup(groupCountryKey, country);

        verify(setOperations).add(groupCountryKey, country);
    }

    @Test
    void testCanUserJoinGroup() {
        String groupCountryKey = "groupCountryMapping:1";
        String country = "Canada";

        SetOperations<String, String> setOperations = mock(SetOperations.class);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember(groupCountryKey, country)).thenReturn(false);

        boolean result = redisService.canUserJoinGroup(1L, country);

        assertFalse(!result);
        verify(setOperations).isMember(groupCountryKey, country);
    }

    @Test
    void testIncrementGroupLeaderBoardScore() {
        Long groupId = 1L;
        Long userId = 1L;
        int scoreIncrement = 5;
        String groupLeaderBoardKey = "groupLeaderBoard:" + groupId;
        String member = "User:" + userId;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        redisService.incrementGroupLeaderBoardScore(groupId, userId, scoreIncrement);

        verify(zSetOperations).incrementScore(groupLeaderBoardKey, member, scoreIncrement);
    }

    @Test
    void testGetCountryLeaderBoard() {
        String leaderboardKey = "countryLeaderBoard:1";
        Set<ZSetOperations.TypedTuple<String>> expectedSet = Set.of();  // Assume empty set or mocked data

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores(leaderboardKey, 0, -1)).thenReturn(expectedSet);

        Set<ZSetOperations.TypedTuple<String>> result = redisService.getCountryLeaderBoard(leaderboardKey);

        assertEquals(expectedSet, result);
        verify(zSetOperations).reverseRangeWithScores(leaderboardKey, 0, -1);
    }

    @Test
    void testGetRankOfUserInGroupLeaderBoard() {
        Long groupId = 1L;
        Long userId = 1L;
        String leaderboardKey = "groupLeaderBoard:" + groupId;
        Long rank = 2L;  // Mocked rank

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRank(leaderboardKey, "User:" + userId)).thenReturn(rank);

        Integer resultRank = redisService.getRankOfUserInGroupLeaderBoard(groupId, userId);

        assertEquals(Integer.valueOf(rank.intValue() + 1), resultRank);
        verify(zSetOperations).reverseRank(leaderboardKey, "User:" + userId);
    }

    @Test
    void testCreateCountryLeaderBoard() {
        Long tournamentId = 1L;
        String key = "countryLeaderBoard:" + tournamentId;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        redisService.createCountryLeaderBoard(tournamentId);

        verify(zSetOperations).add(key, "TURKEY", 0);
        verify(zSetOperations).add(key, "UNITED_STATES", 0);
        verify(zSetOperations).add(key, "UNITED_KINGDOM", 0);
        verify(zSetOperations).add(key, "FRANCE", 0);
        verify(zSetOperations).add(key, "GERMANY", 0);
        verifyNoMoreInteractions(zSetOperations);
        verify(redisTemplate).opsForZSet();
    }

    @Test
    void testIncrementCountryLeaderBoardScore() {
        String country = "GERMANY";
        int scoreIncrement = 10;
        Long tournamentId = 1L;
        String countryLeaderBoardKey = "countryLeaderBoard:" + tournamentId;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        redisService.incrementCountryLeaderBoardScore(country, scoreIncrement, tournamentId);

        verify(zSetOperations).incrementScore(countryLeaderBoardKey, country, scoreIncrement);

        verifyNoMoreInteractions(zSetOperations);
        verify(redisTemplate).opsForZSet();
    }

}