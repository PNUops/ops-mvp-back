package com.ops.ops.modules.team.domain.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;

public interface TeamLikeRepository extends JpaRepository<TeamLike, Long> {
	Optional<TeamLike> findByMemberIdAndTeam(Long memberId, Team team);

	List<TeamLike> findByMemberIdAndTeamIn(Long id, List<Team> teams);
}
