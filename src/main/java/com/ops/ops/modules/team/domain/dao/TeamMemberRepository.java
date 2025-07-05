package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.TeamMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByTeamId(Long id);

    TeamMember findByMemberId(Long id);
}
