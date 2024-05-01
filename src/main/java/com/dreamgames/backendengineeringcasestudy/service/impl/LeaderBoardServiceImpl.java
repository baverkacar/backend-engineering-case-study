package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.repository.GroupInfoRepository;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private final GroupInfoRepository groupInfoRepository;
    private final RedisService redisService;

    @Override
    public List<GroupLeaderBoard> getGroupLeaderBoard(Long groupId) {
        Set<ZSetOperations.TypedTuple<String>> groupLeaderBoard = redisService.getGroupLeaderBoard(groupId);
        return mapToGroupLeaderBoardList(groupLeaderBoard);
    }

    private List<GroupLeaderBoard> mapToGroupLeaderBoardList(Set<ZSetOperations.TypedTuple<String>> rawScores) {
        return rawScores.stream().map(scoreEntry -> {
            String member = scoreEntry.getValue(); // Member verisi, örneğin "User:2"
            Long userId = Long.parseLong(member.split(":")[1]); // "User:2" değerinden 2'yi çıkar
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
}
