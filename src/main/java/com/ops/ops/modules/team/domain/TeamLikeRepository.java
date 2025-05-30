package com.ops.ops.modules.team.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamLikeRepository extends JpaRepository<TeamLike, Long> {
	Optional<TeamLike> findByMemberIdAndTeam(Long memberId, Team team);
}
