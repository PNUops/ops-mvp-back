package com.ops.ops.modules.contest.application;

import static com.ops.ops.modules.contest.exception.ContestExceptionType.NOT_FOUND_CURRENT_CONTEST;

import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.application.dto.response.ContestResponse;
import com.ops.ops.modules.contest.application.dto.response.VoteResponse;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.convenience.TeamLikeConvenience;
import com.ops.ops.modules.team.application.convenience.TeamSortConvenience;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamSort;
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
    private final TeamSortConvenience teamSortConvenience;
    private final ContestConvenience contestConvenience;

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
        final TeamSort teamSort = teamSortConvenience.getValidateExistTeamSort();
        return teamLikeConvenience.getAllTeamSummaries(teams, member, teamSort.getMode());
    }

    public List<TeamSummaryResponse> getCurrentContestTeamSummaries(final Member member) {
        final List<Team> teams = findTeamsOfCurrentContest();
        final TeamSort teamSort = teamSortConvenience.getValidateExistTeamSort();
        return teamLikeConvenience.getAllTeamSummaries(teams, member, teamSort.getMode());
    }

    private List<Team> findTeamsOfCurrentContest() {
        final Contest contest = contestRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new ContestException(NOT_FOUND_CURRENT_CONTEST));
        return teamConvenience.findAllByContestId(contest.getId());
    }

    public VoteResponse getVotePeriod(Long contestId) {
        final Contest contest = contestConvenience.getValidateExistContest(contestId);
        return new VoteResponse(contest.getVoteStartAt(), contest.getVoteEndAt());
    }
}
