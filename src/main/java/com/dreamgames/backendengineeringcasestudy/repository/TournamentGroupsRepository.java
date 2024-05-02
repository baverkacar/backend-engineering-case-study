package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.domain.TournamentGroups;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TournamentGroupsRepository extends JpaRepository<TournamentGroups, Long> {
    @Query("SELECT tg FROM TournamentGroups tg WHERE tg.tournament.tournamentId = :tournamentId")
    List<TournamentGroups> findByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tg.groupSize FROM TournamentGroups tg WHERE tg.groupId = :groupId")
    Integer findGroupSizeByGroupId(@Param("groupId") Long groupId);
}



