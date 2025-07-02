package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestCommandService {
    private final ContestRepository contestRepository;

    public void createContest(final String contestName) {
        boolean isCurrent = contestName.contains("6") && contestName.contains("창의융합");
        final Contest contest = Contest.of(contestName, isCurrent);

        contestRepository.save(contest);
    }

    public void updateContest(final Long contestId, final String newContestName) {
        final Contest contest = validateAndGetContestById(contestId);
        contest.updateContestName(newContestName);
    }

    private Contest validateAndGetContestById(final Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestExceptionType.NOT_FOUND_CONTEST));
    }
}
