package com.dreamgames.backendengineeringcasestudy.model.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CountryLeaderBoard {
    private String countryName;
    private Integer score;
}
