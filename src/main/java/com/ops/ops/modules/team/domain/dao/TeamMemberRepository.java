package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.TeamMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByTeamId(Long id);

    Optional<TeamMember> findByMemberId(Long memberId);
}
