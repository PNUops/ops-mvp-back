package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByTeamId(Long id);

    Optional<TeamMember> findByMemberId(Long memberId);

    Optional<TeamMember> findByMemberIdAndTeam(Long memberId, Team team);

    @Modifying
    @Query("DELETE FROM TeamMember tm WHERE tm.team.id = :teamId")
    void deleteAllByTeamId(Long teamId);
}
