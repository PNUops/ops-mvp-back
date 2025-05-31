package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByIdAndIsDeletedFalse(Long teamId);

}
