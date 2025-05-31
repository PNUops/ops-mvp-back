package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.application.dto.TeamRank;
import com.ops.ops.modules.team.application.dto.response.TeamLikeRankingResponse;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
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
	private final TeamLikeQueryService teamLikeQueryService;

	public List<TeamSubmissionStatusResponse> getAllTeamSubmissions() {
		return teamRepository.findAll()
			.stream()
			.map(TeamSubmissionStatusResponse::fromEntity)
			.collect(Collectors.toList());
	}

	public List<TeamLikeRankingResponse> getTeamLikeRanking() {
		// 1. 팀별 좋아요 수를 Map 형태로 가져오기
		Map<Long, Integer> likeCountMap = teamLikeQueryService.getTeamLikeCountMap();

		// 2. 팀 좋아요 랭킹 리스트 생성
		List<TeamRank> rankedList = teamLikeQueryService.getTeamRankList(likeCountMap);

		// 3. 랭킹을 Competition Ranking 방식으로 적용하여 반환
		return teamLikeQueryService.applyCompetitionRanking(rankedList);
	}
}
