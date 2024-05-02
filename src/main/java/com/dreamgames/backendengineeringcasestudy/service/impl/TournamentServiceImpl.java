package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.*;
import com.dreamgames.backendengineeringcasestudy.exception.UnClaimedRewardFoundException;
import com.dreamgames.backendengineeringcasestudy.exception.UserCanNotEnterTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserEnteredTournamentBeforeException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.repository.*;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentServiceImpl implements TournamentService {

    private final LeaderBoardService leaderBoardService;
    private final RedisService redisService;

    private final UserRepository userRepository;
    private final TournamentGroupsRepository tournamentGroupsRepository;
    private final TournamentRepository tournamentRepository;
    private final GroupInfoRepository groupInfoRepository;
    private final TournamentRewardsRepository tournamentRewardsRepository;


    /**
     * Allows a user to enter an active tournament and returns the current group leaderboard.
     * Checks if there is an active tournament and verifies the user's eligibility based on level and coins.
     * It ensures that the user has not already entered the current tournament and manages the addition of the user to a group.
     * If necessary, a new group is created.
     *
     * @param userId the ID of the user attempting to enter the tournament
     * @return a list of GroupLeaderBoard detailing the current state of the group leaderboard after entry
     * @throws IllegalStateException if no active tournament exists or if the user does not meet the participation criteria
     */
    @Override
    @Transactional
    public List<GroupLeaderBoard> enterTournament(Long userId) {

        // Check user's eligibility
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user found with id: " + userId));

        // Check active tournament existence
        redisService.checkActiveTournament();
        Long activeTournamentId = redisService.getActiveTournamentId();
        Tournament activeTournament = tournamentRepository.findTournamentByTournamentId(activeTournamentId);


        if (activeTournament == null) {
            throw new UserCanNotEnterTournamentException("Active tournament not found for id: " + activeTournamentId);
        }

        // Check user level and coin
        User user = userRepository.findByUserIdAndLevelGreaterThanEqualAndCoinsGreaterThanEqual(userId, 20, 1000)
                .orElseThrow(() -> new UserCanNotEnterTournamentException("User does not meet the requirements or does not exist."));

        // Check user has entered current tournament before
        if (groupInfoRepository.findByTournamentIdAndUserId(activeTournamentId, userId).isPresent()) {
            throw new UserEnteredTournamentBeforeException("User is already registered in the current active tournament.");
        }

        // check reward claim
        if (tournamentRewardsRepository.existsUnclaimedRewardsForUser(userId)) {
            throw new UserCanNotEnterTournamentException("User has unclaimed rewards and cannot enter a new tournament.");
        }
        List<TournamentGroups> tournamentGroups = tournamentGroupsRepository.findByTournamentId(activeTournamentId);

        if (tournamentGroups.isEmpty()) {
            TournamentGroups newGroup = createTournamentGroup(userId, activeTournament, user);
            log.info("New group created with id: {}", newGroup.getGroupId());
            return leaderBoardService.getGroupLeaderBoardWithGroupId(newGroup.getGroupId());
        }
        for (TournamentGroups tournamentGroup : tournamentGroups) {
            Long groupId = tournamentGroup.getGroupId();

            // Check Countries for group
            if (!redisService.canUserJoinGroup(groupId, user.getCountry())) {
                continue;
            }

            int groupSize = tournamentGroupsRepository.findGroupSizeByGroupId(groupId) + 1;
            GroupInfo groupInfo = GroupInfo.builder()
                    .group(tournamentGroup)
                    .user(user)
                    .score(0)
                    .hasGroupBegan(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            groupInfoRepository.save(groupInfo);

            tournamentGroup.setGroupSize(groupSize);
            tournamentGroupsRepository.save(tournamentGroup);

            // update redis caches
            redisService.updateGroupLeaderBoard(groupId, userId, 0);
            String groupCountryKey = "groupCountryMapping:" + groupId;
            redisService.addCountryToGroup(groupCountryKey, user.getCountry());

            // Check group size for begin
            if (groupSize == 5) {
                groupInfoRepository.updateHasGroupBegunForAllOccurrences(groupId);
            }
            return leaderBoardService.getGroupLeaderBoardWithGroupId(tournamentGroup.getGroupId());
        }
        TournamentGroups newGroup = createTournamentGroup(userId, activeTournament, user);
        log.info("New group created with id: {}", newGroup.getGroupId());
        return leaderBoardService.getGroupLeaderBoardWithGroupId(newGroup.getGroupId());
    }


    /**
     * Creates a new tournament and sets it as active. This function is scheduled to run daily at midnight.
     * It initializes the start and end times of the tournament, marks it as active, and persists it to the database.
     *
     * @return the ID of the newly created tournament
     */
    @Transactional
    @Override
    public Long createTournament() {
        Tournament newTournament = Tournament.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(20))
                .status("Active")
                .build();
        newTournament = tournamentRepository.save(newTournament);
        log.info("[TOURNAMENT SERVICE] New tournament created with id {}" , newTournament.getTournamentId());
        return newTournament.getTournamentId();
    }


    /**
     * Closes the currently active tournament. This function is scheduled to run daily at 8 PM.
     * It updates the tournament's status to 'Completed' and persists the change to the database.
     */
    @Transactional
    @Override
    public void closeTournament() {
        Tournament activeTournament = tournamentRepository.findFirstByStatus("Active");
        if (activeTournament != null) {
            activeTournament.setStatus("Completed");
            tournamentRepository.save(activeTournament);
        }
        log.info("[TOURNAMENT SERVICE] Tournament closed.");
    }


    /**
     * Determines the top two players in each active tournament group and assigns rewards based on their rank.
     * This function fetches the active tournament ID, retrieves all group information for begun groups,
     * and then calculates the rewards for the top two participants in each group based on their scores.
     */
    @Override
    @Transactional
    public void specifyRewardWinners() {
        Long activeTournamentId = redisService.getActiveTournamentId();
        List<GroupInfo> groupInfos = groupInfoRepository.findByTournamentIdAndGroupBegun(activeTournamentId);
        Set<Long> uniqueGroupIds = groupInfos.stream()
                .map(groupInfo -> groupInfo.getGroup().getGroupId())
                .collect(Collectors.toSet());

        for (Long groupId : uniqueGroupIds) {
            Set<ZSetOperations.TypedTuple<String>> scores = redisService.getGroupLeaderBoard(groupId);
            List<String> userIds = scores.stream()
                    .map(ZSetOperations.TypedTuple::getValue)
                    .limit(2)
                    .toList();

            if (!userIds.isEmpty()) {
                createTournamentReward(userIds.get(0), activeTournamentId, 10000);
                createTournamentReward(userIds.get(1), activeTournamentId, 5000);
            }
        }
    }


    /**
     * Claims all unclaimed tournament rewards for a given user.
     * This method performs several operations:
     * 1. Checks if the user has any unclaimed rewards. If not, throws an exception.
     * 2. Sums up the total coins won by the user in all unclaimed rewards.
     * 3. Updates the user's coin balance in the user repository with the total coins won.
     * 4. Marks all the user's unclaimed rewards as claimed in the rewards repository.
     *
     * @param userId The ID of the user claiming the rewards.
     * @throws IllegalArgumentException If no unclaimed rewards are found for the user.
     * @throws UserNotFoundException If the user with the given ID does not exist.
     * @transactional Ensures the entire process is handled in a single transaction, ensuring data integrity.
     */
    @Override
    @Transactional
    public void claimTournamentsReward(Long userId) {
        // 1. Check if user has any unclaimed rewards
        if (!tournamentRewardsRepository.existsByUserIdAndClaimedFalse(userId)) {
            throw new UnClaimedRewardFoundException("No unclaimed rewards found for user ID: " + userId);
        }

        // 2. Sum up the coins won by this user in unclaimed rewards
        int totalCoinsWon = tournamentRewardsRepository.sumUnclaimedCoinsByUserId(userId);

        // 3. Update user's coins and set rewards as claimed
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.setCoins(user.getCoins() + totalCoinsWon);
        userRepository.save(user);

        // Mark all the rewards as claimed
        tournamentRewardsRepository.markRewardsAsClaimedForUser(userId);

        log.info("User {} has claimed their rewards totaling {} coins.", userId, totalCoinsWon);
    }


    /**
     * Creates a reward record for a user who participated in a tournament based on their performance.
     * @param userWithPrefix The composite string key of the user in the format "User:UserId" from the Redis leaderboard.
     * @param tournamentId The ID of the tournament for which the reward is being specified.
     * @param coinsWon The amount of coins won by the user, depending on their rank in the leaderboard.
     */
    private void createTournamentReward(String userWithPrefix, Long tournamentId, int coinsWon) {
        Long userId = Long.parseLong(userWithPrefix.split(":")[1]);

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament ID"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        TournamentRewards reward = new TournamentRewards();
        reward.setUser(user);
        reward.setTournament(tournament);
        reward.setCoinsWon(coinsWon);
        reward.setClaimed(false);
        reward.setCreatedAt(LocalDateTime.now());
        reward.setUpdatedAt(LocalDateTime.now());

        tournamentRewardsRepository.save(reward);
        log.info("Reward of {} coins created for user {} in tournament {}", coinsWon, userId, tournamentId);
    }


    /**
     * Helper method to create a tournament group when a user enters a tournament.
     * This method initializes the group in the database and Redis, sets up the group's leaderboard and country mappings.
     *
     * @param userId the ID of the user entering the tournament
     * @param activeTournament the active tournament in which the group is being created
     * @param user the User entity of the user entering the tournament
     * @return the newly created TournamentGroups entity
     */
    private TournamentGroups createTournamentGroup(Long userId, Tournament activeTournament, User user) {
        TournamentGroups newGroup = TournamentGroups.builder()
                .tournament(activeTournament)
                .groupSize(1)
                .build();
        newGroup = tournamentGroupsRepository.save(newGroup);

        GroupInfo groupInfo = GroupInfo.builder()
                .group(newGroup)
                .user(user)
                .score(0)
                .hasGroupBegan(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        groupInfoRepository.save(groupInfo);

        // Initialize Group and LeaderBoard and Group country mapping in Redis.
        redisService.initializeGroup(newGroup.getGroupId());
        redisService.updateGroupLeaderBoard(newGroup.getGroupId(), userId, 0);

        String groupCountryKey = "groupCountryMapping:" + newGroup.getGroupId();
        redisService.addCountryToGroup(groupCountryKey, user.getCountry());
        return newGroup;
    }
}
