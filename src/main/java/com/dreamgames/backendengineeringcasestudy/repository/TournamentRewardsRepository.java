package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.domain.TournamentRewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRewardsRepository extends JpaRepository<TournamentRewards, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM TournamentRewards r WHERE r.user.userId = :userId AND r.claimed = false")
    boolean existsUnclaimedRewardsForUser(@Param("userId") Long userId);

    @Query("SELECT SUM(r.coinsWon) FROM TournamentRewards r WHERE r.user.userId = :userId AND r.claimed = false")
    int sumUnclaimedCoinsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE TournamentRewards r SET r.claimed = true WHERE r.user.userId = :userId")
    void markRewardsAsClaimedForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) > 0 FROM TournamentRewards r WHERE r.user.userId = :userId AND r.claimed = false")
    boolean existsByUserIdAndClaimedFalse(@Param("userId") Long userId);
}
