package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestCommandService {
    private final ContestRepository contestRepository;

    private final ContestConvenience contestConvenience;
    private final TeamConvenience teamConvenience;

    public void createContest(final String contestName) {
        contestConvenience.checkDuplicateContestName(contestName);
        // 만약 제 6회 창의융합 대회를 직접 등록하는 상황일 때 isCurrent를 true로 설정하도록
        final boolean isCurrent = contestName.contains("6회") && contestName.contains("창의융합");
        final Contest contest = Contest.builder()
                .contestName(contestName)
                .isCurrent(isCurrent)
                .build();
        contestRepository.save(contest);
    }

    public void updateContest(final Long contestId, final String newContestName) {
        contestConvenience.checkDuplicateContestName(newContestName);
        final Contest contest = contestConvenience.getValidateExistContest(contestId);
        contest.updateContestName(newContestName);
    }

    public void deleteContest(final Long contestId) {
        final Contest contest = contestConvenience.getValidateExistContest(contestId);
        teamConvenience.checkAllContestDelete(contestId);
        contestRepository.delete(contest);
    }
}
