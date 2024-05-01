package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import com.dreamgames.backendengineeringcasestudy.domain.Tournament;
import com.dreamgames.backendengineeringcasestudy.domain.TournamentGroups;
import com.dreamgames.backendengineeringcasestudy.domain.User;
import com.dreamgames.backendengineeringcasestudy.exception.UserCanNotEnterTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserEnteredTournamentBeforeException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.repository.GroupInfoRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentGroupsRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        User user = userRepository.findByUserIdAndLevelGreaterThanEqualAndCoinsGreaterThanEqual(userId, 20, 1000)
                .orElseThrow(() -> new UserCanNotEnterTournamentException("User does not meet the requirements or does not exist."));

        // Check user has entered current tournament before
        if (groupInfoRepository.findByTournamentIdAndUserId(activeTournamentId, userId).isPresent()) {
            throw new UserEnteredTournamentBeforeException("User is already registered in the current active tournament.");
        }

        // TODO: User Reward kontrolünü yap.

        List<TournamentGroups> tournamentGroups = tournamentGroupsRepository.findByTournamentId(activeTournamentId);

        if (tournamentGroups.isEmpty()) {
            TournamentGroups newGroup = createTournamentGroup(userId, activeTournament, user);
            log.info("New group created with id: {}", newGroup.getGroupId());
            return leaderBoardService.getGroupLeaderBoard(newGroup.getGroupId());
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
                    .hasGroupBegun(false)
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
            return leaderBoardService.getGroupLeaderBoard(tournamentGroup.getGroupId());
        }
        TournamentGroups newGroup = createTournamentGroup(userId, activeTournament, user);
        log.info("New group created with id: {}", newGroup.getGroupId());
        return leaderBoardService.getGroupLeaderBoard(newGroup.getGroupId());
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
                .hasGroupBegun(false)
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
