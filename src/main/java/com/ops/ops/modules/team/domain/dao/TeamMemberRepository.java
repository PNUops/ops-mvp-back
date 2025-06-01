package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByTeamId(Long id);
}
