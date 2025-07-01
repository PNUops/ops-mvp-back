package com.ops.ops.modules.contest.domain.dao;

import com.ops.ops.modules.contest.domain.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
}
