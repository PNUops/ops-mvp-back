package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestTeamQueryService {
    private final ContestTeamRepository contestTeamRepository;

    public boolean isContestContainingAnyTeam(final Contest contest) {
        return contestTeamRepository.existsByContestAndIsDeletedFalse(contest);
    }
}
