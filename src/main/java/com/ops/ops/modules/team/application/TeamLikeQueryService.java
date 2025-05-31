package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.application.dto.TeamLikeCountDto;
import com.ops.ops.modules.team.application.dto.TeamRank;
import com.ops.ops.modules.team.application.dto.response.TeamLikeRankingResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamLikeQueryService {

	private final TeamRepository teamRepository;
	private final TeamLikeRepository teamLikeRepository;

	public Map<Long, Integer> getTeamLikeCountMap() {
		List<TeamLikeCountDto> likedList = teamLikeRepository.findTeamLikeCountGrouped();

		Map<Long, Integer> teamLikeCountMap = new HashMap<>();
		for (TeamLikeCountDto dto : likedList) {
			teamLikeCountMap.put(dto.getTeamId(), dto.getLikeCount().intValue());
		}
		return teamLikeCountMap;
	}

	public List<TeamRank> getTeamRankList(Map<Long, Integer> teamLikeCountMap) {
		List<TeamRank> teamRankList = new ArrayList<>();
		List<Team> teamList = teamRepository.findAll();

		for (Team team : teamList) {
			if (team.getIsDeleted()) {
				continue;
			}
			int likeCount = teamLikeCountMap.getOrDefault(team.getId(), 0);
			TeamRank teamRank = new TeamRank(
				team.getTeamName(),
				team.getProjectName(),
				likeCount
			);
			teamRankList.add(teamRank);
		}

		// 좋아요 수를 기준으로 내림차순 정렬
		teamRankList.sort((a, b) -> Integer.compare(b.likeCount(), a.likeCount()));

		return teamRankList;
	}

	// competition ranking 방식으로 랭킹을 부여
	public List<TeamLikeRankingResponse> applyCompetitionRanking(List<TeamRank> teamRankList) {
		List<TeamLikeRankingResponse> teamLikeRankingResponses = new ArrayList<>();

		int currentRank = 1, nextRank = 1, previousLikeCount = -1;
		for (TeamRank team : teamRankList) {
			int likeCount = team.likeCount();
			if (likeCount != previousLikeCount) {
				currentRank = nextRank;
			}

			teamLikeRankingResponses.add(
				TeamLikeRankingResponse.of(currentRank, team.teamName(), team.projectName(), likeCount)
			);
			previousLikeCount = likeCount;
			nextRank++;
		}

		return teamLikeRankingResponses;
	}
}
