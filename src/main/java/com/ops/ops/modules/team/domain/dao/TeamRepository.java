package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
