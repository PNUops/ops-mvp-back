package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.application.dto.response.ContestResponse;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestQueryService {
    private final ContestRepository contestRepository;
    private final TeamRepository teamRepository;
    private final TeamQueryService teamQueryService;

    public List<ContestResponse> getAllContests() {
        List<Contest> contests = contestRepository.findAll();

        return contests.stream()
                .map(contest -> new ContestResponse(
                        contest.getId(),
                        contest.getContestName(),
                        contest.getUpdatedAt()
                ))
                .toList();
    }


    public List<TeamSummaryResponse> getContestTeamSummaries(final Long contestId, final Member member) {
        final List<Team> teams = teamRepository.findByContestId(contestId);
        return teamQueryService.getAllTeamSummaries(teams, member);
    }

    public List<TeamSummaryResponse> getCurrentContestTeamSummaries(final Member member) {
        final List<Team> teams = findTeamsOfCurrentContest();
        return teamQueryService.getAllTeamSummaries(teams, member);
    }

    public List<Team> findTeamsOfCurrentContest() {
        final Contest contest = contestRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new ContestException(ContestExceptionType.NOT_FOUND_CURRENT_CONTEST));
        return teamRepository.findByContestId(contest.getId());
    }
}
