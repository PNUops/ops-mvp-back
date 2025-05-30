package com.ops.ops.modules.team.domain.dao;

import java.util.List;

import com.ops.ops.modules.team.domain.TeamComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamCommentRepository extends JpaRepository<TeamComment, Long> {
	List<TeamComment> findAllByTeamId(Long id);
}
