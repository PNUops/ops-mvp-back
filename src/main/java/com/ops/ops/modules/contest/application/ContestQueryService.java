package com.ops.ops.modules.contest.application;

import static com.ops.ops.modules.contest.exception.ContestExceptionType.NOT_FOUND_CURRENT_CONTEST;

import com.ops.ops.modules.contest.application.dto.response.ContestResponse;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.convenience.TeamLikeConvenience;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.Team;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestQueryService {

    private final ContestRepository contestRepository;

    private final TeamConvenience teamConvenience;
    private final TeamLikeConvenience teamLikeConvenience;

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
        final List<Team> teams = teamConvenience.findAllByContestId(contestId);
        return teamLikeConvenience.getAllTeamSummaries(teams, member);
    }

    public List<TeamSummaryResponse> getCurrentContestTeamSummaries(final Member member) {
        final List<Team> teams = findTeamsOfCurrentContest();
        return teamLikeConvenience.getAllTeamSummaries(teams, member);
    }

    private List<Team> findTeamsOfCurrentContest() {
        final Contest contest = contestRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new ContestException(NOT_FOUND_CURRENT_CONTEST));
        return teamConvenience.findAllByContestId(contest.getId());
    }
}
