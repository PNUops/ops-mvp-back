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
    private final ContestTeamQueryService contestTeamQueryService;

    public void createContest(final String contestName) {
        validateDuplicateContestName(contestName);
        boolean isCurrent = contestName.contains("6회") && contestName.contains("창의융합");
        final Contest contest = Contest.of(contestName, isCurrent);
        contestRepository.save(contest);
    }

    private void validateDuplicateContestName(String contestName) {
        if (contestRepository.existsByContestName(contestName)) {
            throw new ContestException(ContestExceptionType.CONTEST_NAME_ALREADY_EXIST);
        }
    }

    public void updateContest(final Long contestId, final String newContestName) {
        validateDuplicateContestName(newContestName);
        final Contest contest = validateAndGetContestById(contestId);
        contest.updateContestName(newContestName);
    }

    public Contest validateAndGetContestById(final Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestExceptionType.NOT_FOUND_CONTEST));
    }

    public void deleteContest(final Long contestId) {
        final Contest contest = validateAndGetContestById(contestId);
        if (contestTeamQueryService.isContestContainingAnyTeam(contest)) {
            throw new ContestException(ContestExceptionType.CONTEST_HAS_TEAMS);
        }
        contestRepository.delete(contest);
    }
}
