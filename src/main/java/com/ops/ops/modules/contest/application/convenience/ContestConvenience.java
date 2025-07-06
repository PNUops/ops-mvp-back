package com.ops.ops.modules.contest.application.convenience;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContestConvenience {

    private final ContestRepository contestRepository;

    public Contest getValidateExistContest(final Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestExceptionType.NOT_FOUND_CONTEST));
    }

    public void validateNotCurrentContest(final Long contestId) {
        Contest contest = getValidateExistContest(contestId);
        if (contest.getIsCurrent()) {
            throw new ContestException(ContestExceptionType.CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT);
        }
    }
}
