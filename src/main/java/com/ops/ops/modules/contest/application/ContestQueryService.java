package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.application.dto.response.ContestResponse;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.ContestTeam;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.domain.dao.ContestTeamRepository;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestQueryService {
    private final ContestRepository contestRepository;
    private final ContestTeamRepository contestTeamRepository;
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

    private List<Long> getTeamIdsByContestId(final Long contestId) {
        List<ContestTeam> contestTeams = contestTeamRepository.findAllByContestId(contestId);
        return contestTeams.stream()
                .map(ContestTeam::getTeamId)
                .toList();
    }

    public List<TeamSummaryResponse> getAllTeamSummariesByContest(final Long contestId, final Member member) {
        List<Long> contestTeamIds = getTeamIdsByContestId(contestId);
        return teamQueryService.getAllTeamSummaries(contestTeamIds, member);
    }
}
