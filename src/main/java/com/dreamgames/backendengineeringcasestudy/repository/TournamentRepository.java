package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.domain.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("SELECT t FROM Tournament t WHERE t.status = :status")
    Tournament findFirstByStatus(@Param("status") String status);

    @Query("SELECT t FROM Tournament t WHERE t.tournamentId = :id")
    Tournament findTournamentByTournamentId(@Param("id") Long id);
}
