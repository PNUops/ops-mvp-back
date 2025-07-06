package com.ops.ops.modules.contest.domain.dao;

import java.util.Optional;

import com.ops.ops.modules.contest.domain.Contest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    boolean existsByContestName(String contestName);

    Optional<Contest> findByIsCurrentTrue();
}
