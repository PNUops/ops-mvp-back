package com.ops.ops.modules.team.application;

import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.team.application.dto.TeamLikeCountDto;
import com.ops.ops.modules.team.application.dto.TeamRank;
import com.ops.ops.modules.team.application.dto.response.TeamLikeRankingResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
import com.ops.ops.modules.team.application.dto.response.TeamVoteRateResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamAdminQueryService {

	private final TeamRepository teamRepository;
	private final TeamLikeRepository teamLikeRepository;

	private final MemberConvenience memberConvenience;
	private final ContestConvenience contestConvenience;

	public List<TeamSubmissionStatusResponse> getAllTeamSubmissions() {
		final Contest currentContest = contestConvenience.get6thContest();

		return teamRepository.findAllByContestId(currentContest.getId())
			.stream()
			.map(TeamSubmissionStatusResponse::fromEntity)
			.collect(Collectors.toList());
	}

	public List<TeamLikeRankingResponse> getTeamLikeRanking() {
		Map<Long, Integer> likeCountMap = getTeamLikeCountMap();
		List<TeamRank> rankedList = getTeamRankList(likeCountMap);
		return applyCompetitionRanking(rankedList);
	}

	public TeamVoteRateResponse getVoteRate() {
		final Contest currentContest = contestConvenience.get6thContest();
		
		List<Team> currentTeams = teamRepository.findAllByContestId(currentContest.getId());
		
		long totalMemberCount = memberConvenience.countTotalMember();
		long votedMemberCount = teamLikeRepository.countDistinctMemberIdsByIsLikedTrueAndTeams(currentTeams);
		long totalVoteCount = teamLikeRepository.countByIsLikedTrueAndTeams(currentTeams);

		double voteRate = 0.0;
		if (totalMemberCount > 0) {
			voteRate = Math.round((double) votedMemberCount / totalMemberCount * 1000) / 10.0;
		}

        return new TeamVoteRateResponse(voteRate, (int) totalVoteCount);
	}

	private Map<Long, Integer> getTeamLikeCountMap() {
		final Contest currentContest = contestConvenience.get6thContest();
		
		List<Team> currentTeams = teamRepository.findAllByContestId(currentContest.getId());
		List<TeamLikeCountDto> likedList = teamLikeRepository.findTeamLikeCountGroupedByTeams(currentTeams);

		Map<Long, Integer> teamLikeCountMap = new HashMap<>();
		for (TeamLikeCountDto dto : likedList) {
			teamLikeCountMap.put(dto.getTeamId(), dto.getLikeCount().intValue());
		}
		return teamLikeCountMap;
	}

	private List<TeamRank> getTeamRankList(Map<Long, Integer> teamLikeCountMap) {
		final Contest currentContest = contestConvenience.get6thContest();
		
		List<TeamRank> teamRankList = new ArrayList<>();
		List<Team> teamList = teamRepository.findAllByContestId(currentContest.getId());

		for (Team team : teamList) {
			if (team.getIsDeleted()) {
				continue;
			}
			int likeCount = teamLikeCountMap.getOrDefault(team.getId(), 0);
			TeamRank teamRank = new TeamRank(
				team.getId(),
				team.getTeamName(),
				team.getProjectName(),
				likeCount,
				team.getDisplayOrder(),
				team.getAwardTitle(),
				team.getAwardBadgeColor(),
				team.getAwardBadgeSize()
			);
			teamRankList.add(teamRank);
		}

		teamRankList.sort((a, b) -> Integer.compare(b.likeCount(), a.likeCount()));

		return teamRankList;
	}

	private List<TeamLikeRankingResponse> applyCompetitionRanking(List<TeamRank> teamRankList) {
		List<TeamLikeRankingResponse> teamLikeRankingResponses = new ArrayList<>();

		int currentRank = 1, nextRank = 1, previousLikeCount = -1;
		for (TeamRank team : teamRankList) {
			int likeCount = team.likeCount();
			if (likeCount != previousLikeCount) {
				currentRank = nextRank;
			}

			teamLikeRankingResponses.add(
				new TeamLikeRankingResponse(
					currentRank, 
					team.teamName(), 
					team.projectName(), 
					likeCount,
					team.displayOrder(),
					team.awardTitle(),
					team.awardBadgeColor(),
					team.awardBadgeSize()
				)
			);
			previousLikeCount = likeCount;
			nextRank++;
		}

		return teamLikeRankingResponses;
	}
}
