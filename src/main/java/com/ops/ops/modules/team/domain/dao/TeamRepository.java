package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByContestId(Long contestId);

    List<Team> findByContestId(Long contestId);

    List<Team> findAllByContestId(Long contestId);
}
