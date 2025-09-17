package com.ops.ops.modules.contest.domain.dao;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.ops.ops.modules.contest.domain.Contest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    boolean existsByContestName(String contestName);

    Optional<Contest> findByIsCurrentTrue();

    @Lock(PESSIMISTIC_WRITE)
    @Query("select c from Contest c where c.id = :contestId")
    Optional<Contest> findByIdForUpdate(final Long contestId);
}
