package com.ops.ops.modules.team.domain.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ops.ops.modules.team.application.dto.TeamLikeCountDto;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;

public interface TeamLikeRepository extends JpaRepository<TeamLike, Long> {
	Optional<TeamLike> findByMemberIdAndTeam(Long memberId, Team team);

	@Query("SELECT t.team.id AS teamId, COUNT(t) AS likeCount FROM TeamLike t WHERE t.isLiked = true GROUP BY t.team.id")
	List<TeamLikeCountDto> findTeamLikeCountGrouped();

	@Query("SELECT COUNT(DISTINCT t.memberId) FROM TeamLike t WHERE t.isLiked = true")
	long countDistinctMemberIdsByIsLikedTrue();

	long countByIsLikedTrue();
}
