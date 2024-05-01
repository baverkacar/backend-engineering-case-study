package com.dreamgames.backendengineeringcasestudy.model.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GroupLeaderBoard {
    private Long userId;
    private String username;
    private String country;
    private Integer tournamentScore;
}
