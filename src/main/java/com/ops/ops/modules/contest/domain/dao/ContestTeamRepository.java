package com.ops.ops.modules.contest.domain.dao;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.ContestTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestTeamRepository extends JpaRepository<ContestTeam, Long> {
    boolean existsByContestAndIsDeletedFalse(Contest contest);
}
