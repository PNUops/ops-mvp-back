package com.ops.ops.modules.contest.application.convenience;

import static com.ops.ops.modules.contest.exception.ContestExceptionType.CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT;
import static com.ops.ops.modules.contest.exception.ContestExceptionType.CONTEST_NAME_ALREADY_EXIST;
import static com.ops.ops.modules.contest.exception.ContestExceptionType.NOT_FOUND_CONTEST;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContestConvenience {

    private final ContestRepository contestRepository;

    public Contest getValidateExistContest(final Long contestId) {
        return contestRepository.findById(contestId).orElseThrow(() -> new ContestException(NOT_FOUND_CONTEST));
    }

    public void validateCurrentContest(final Contest contest) {
        if (contest.getIsCurrent()) {
            throw new ContestException(CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT);
        }
    }

    public Contest get6thContest() {
        return contestRepository.findByIsCurrentTrue().orElseThrow(() -> new ContestException(NOT_FOUND_CONTEST));
    }

    public void checkDuplicateContestName(String contestName) {
        if (contestRepository.existsByContestName(contestName)) {
            throw new ContestException(CONTEST_NAME_ALREADY_EXIST);
        }
    }
}
