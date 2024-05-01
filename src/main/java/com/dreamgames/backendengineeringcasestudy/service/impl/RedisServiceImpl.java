package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void createTournament(Long tournamentId) {
        setActiveTournament(true);
        setActiveTournamentId(tournamentId);
        log.info("[REDIS SERVICE] Daily tournament is now active");
        log.info("[REDIS SERVICE] Daily tournament created with id {}", tournamentId);
    }


    /**
     * Initializes a group in Redis.
     * @param groupId The ID of the group to initialize.
     */
    @Override
    public void initializeGroup(Long groupId) {
        String key = "groupLeaderBoard:" + groupId;
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(key, "initialize", 0);
        zSetOps.remove(key, "initialize");
        log.info("[REDIS SERVICE] Initialized group leader board for group ID: {}", groupId);
    }

    /**
     * Updates the leaderboard for a specific group.
     * @param groupId The ID of the group for which the leaderboard is updated.
     * @param userId The ID of the user whose score is being updated.
     * @param score The new score of the user to be set in the leaderboard.
     */
    @Override
    public void updateGroupLeaderBoard(Long groupId, Long userId, Integer score) {
        String key = "groupLeaderBoard:" + groupId;
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        String member = "User:" + userId;
        zSetOps.add(key, member, score);
        log.info("Updated group leader board for group ID: {} with user ID: {} and score: {}", groupId, userId, score);
    }

    /**
     * Closes the currently active tournament in Redis by clearing the tournament's active flag.
     * This function is typically called when a tournament reaches its end time to clean up and prepare for the next tournament.
     */
    @Override
    public void closeTournament() {
        clearActiveTournamentId();
        setActiveTournament(false);
        log.info("[REDIS SERVICE] Daily tournament is now NOT active");
    }

    @Override
    public void checkActiveTournament() {
        if (!hasActiveTournament()) {
            throw new IllegalStateException("There is no active tournament right now."); // TODO: CUSTOM EXCEPTION
        }
    }

    /**
     * Retrieves the ID of the currently active tournament from Redis.
     * This ID is used to fetch tournament-specific data and manage game flow.
     * @return The ID of the currently active tournament, or null if no tournament is active.
     */
    @Override
    public Long getActiveTournamentId() {
        String id = redisTemplate.opsForValue().get("activeTournamentId");
        return id != null ? Long.parseLong(id) : null;    }


    /**
     * Closes the currently active tournament in Redis by clearing the tournament's active flag.
     * This function is typically called when a tournament reaches its end time to clean up and prepare for the next tournament.
     */
    @Override
    public void createCountryLeaderBoard(Long tournamentId) {
        String key = "countryLeaderBoard:" + tournamentId;
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        zSetOps.add(key, "TURKEY", 0);
        zSetOps.add(key, "UNITED_STATES", 0);
        zSetOps.add(key, "UNITED_KINGDOM", 0);
        zSetOps.add(key, "FRANCE", 0);
        zSetOps.add(key, "GERMANY", 0);

        log.info("Country LeaderBoard for Tournament {} initialized.", tournamentId);
    }

    /**
     * Retrieves the leaderboard for a specified group.
     * @param groupId The ID of the group whose leaderboard is to be fetched.
     * @return Returns the leaderboard information for the specified group.
     */
    @Override
    public Set<ZSetOperations.TypedTuple<String>> getGroupLeaderBoard(Long groupId) {
        String key = "groupLeaderBoard:" + groupId;
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.reverseRangeWithScores(key, 0, -1);
    }

    /**
     * Adds a country to a group's country set in Redis.
     * This is used to track the countries that are already represented in a group.
     * @param groupCountryKey Redis key for the group's country set.
     * @param country The country to add to the group's set.
     */
    @Override
    public void addCountryToGroup(String groupCountryKey, String country) {
        redisTemplate.opsForSet().add(groupCountryKey, country);
        log.info("Added country {} to group {}", country, groupCountryKey);
    }

    /**
     * Checks if a user can join a group based on country restrictions.
     * @param groupId The ID of the group to check.
     * @param country The country of the user wanting to join the group.
     * @return true if the user can join the group; false otherwise.
     */
    @Override
    public boolean canUserJoinGroup(Long groupId, String country) {
        String groupCountryKey = "groupCountryMapping:" + groupId;
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        Boolean isMember = setOps.isMember(groupCountryKey, country);
        return isMember != null && !isMember;
    }

    private void setActiveTournament(boolean isActive) {
        redisTemplate.opsForValue().set("hasActiveTournament", String.valueOf(isActive));
    }

    private boolean hasActiveTournament() {
        String result = redisTemplate.opsForValue().get("hasActiveTournament");
        return Boolean.parseBoolean(result);
    }

    private void setActiveTournamentId(Long tournamentId) {
        redisTemplate.opsForValue().set("activeTournamentId", String.valueOf(tournamentId));
    }

    private void clearActiveTournamentId() {
        redisTemplate.delete("activeTournamentId");
    }
}
