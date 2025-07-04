package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestCommandService {
    private final ContestRepository contestRepository;
    private final TeamRepository teamRepository;

    public void createContest(final String contestName) {
        validateDuplicateContestName(contestName);
        // 만약 제 6회 창의융합 대회를 직접 등록하는 상황일 때 isCurrent를 true로 설정하도록
        boolean isCurrent = contestName.contains("6회") && contestName.contains("창의융합");
        final Contest contest = Contest.builder()
                .contestName(contestName)
                .isCurrent(isCurrent)
                .teams(new ArrayList<>())
                .build();
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
        if (teamRepository.existsByContestId(contestId)) {
            throw new ContestException(ContestExceptionType.CONTEST_HAS_TEAMS);
        }
        contestRepository.delete(contest);
    }
}
