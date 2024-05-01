package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.domain.GroupInfo;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupInfoRepository extends JpaRepository<GroupInfo, Long> {
    @Query("SELECT gi FROM GroupInfo gi JOIN gi.group g WHERE g.tournament.tournamentId = :tournamentId AND gi.user.userId = :userId")
    Optional<GroupInfo> findByTournamentIdAndUserId(@Param("tournamentId")Long tournamentId, @Param("userId")Long userId);

    @Query("SELECT g FROM GroupInfo g WHERE g.user.userId = :userId")
    Optional<GroupInfo> findByUserId(@Param("userId")Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE GroupInfo gi SET gi.hasGroupBegun = true WHERE gi.group.groupId = :groupId")
    void updateHasGroupBegunForAllOccurrences(@Param("groupId") Long groupId);
}
