package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.team.application.dto.TeamLikeCountDto;
import com.ops.ops.modules.team.application.dto.TeamRank;
import com.ops.ops.modules.team.application.dto.response.TeamLikeRankingResponse;
import com.ops.ops.modules.team.application.dto.response.TeamVoteRateResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;

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
	private final MemberRepository memberRepository;

	public List<TeamSubmissionStatusResponse> getAllTeamSubmissions() {
		return teamRepository.findAll()
			.stream()
			.map(TeamSubmissionStatusResponse::fromEntity)
			.collect(Collectors.toList());
	}

	public List<TeamLikeRankingResponse> getTeamLikeRanking() {
		// 1. 팀별 좋아요 수를 Map 형태로 가져오기
		Map<Long, Integer> likeCountMap = getTeamLikeCountMap();

		// 2. 팀 좋아요 랭킹 리스트 생성
		List<TeamRank> rankedList = getTeamRankList(likeCountMap);

		// 3. 랭킹을 Competition Ranking 방식으로 적용하여 반환
		return applyCompetitionRanking(rankedList);
	}

	public TeamVoteRateResponse getVoteRate() {
		long totalMemberCount = memberRepository.count(); // 전체 회원 수

		// 좋아요를 누른 사용자 수 (중복 제거)
		long votedMemberCount = teamLikeRepository.countDistinctMemberIdsByIsLikedTrue();

		// 총 좋아요 수 (중복 포함)
		long totalVoteCount = teamLikeRepository.countByIsLikedTrue();

		double voteRate = 0.0;
		if (totalMemberCount > 0) {
			voteRate = Math.round((double) votedMemberCount / totalMemberCount * 1000) / 10.0; // 소수점 1자리 반올림
		}

        return new TeamVoteRateResponse(voteRate, (int) totalVoteCount);
	}

	private Map<Long, Integer> getTeamLikeCountMap() {
		List<TeamLikeCountDto> likedList = teamLikeRepository.findTeamLikeCountGrouped();

		Map<Long, Integer> teamLikeCountMap = new HashMap<>();
		for (TeamLikeCountDto dto : likedList) {
			teamLikeCountMap.put(dto.getTeamId(), dto.getLikeCount().intValue());
		}
		return teamLikeCountMap;
	}

	private List<TeamRank> getTeamRankList(Map<Long, Integer> teamLikeCountMap) {
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
	private List<TeamLikeRankingResponse> applyCompetitionRanking(List<TeamRank> teamRankList) {
		List<TeamLikeRankingResponse> teamLikeRankingResponses = new ArrayList<>();

		int currentRank = 1, nextRank = 1, previousLikeCount = -1;
		for (TeamRank team : teamRankList) {
			int likeCount = team.likeCount();
			if (likeCount != previousLikeCount) {
				currentRank = nextRank;
			}

			teamLikeRankingResponses.add(
				new TeamLikeRankingResponse(currentRank, team.teamName(), team.projectName(), likeCount)
			);
			previousLikeCount = likeCount;
			nextRank++;
		}

		return teamLikeRankingResponses;
	}
}
