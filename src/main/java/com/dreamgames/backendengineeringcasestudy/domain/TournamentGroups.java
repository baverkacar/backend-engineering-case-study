package com.dreamgames.backendengineeringcasestudy.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "tournament_groups")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TournamentGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "group_size")
    private Integer groupSize;
}
