package com.ops.ops.modules.team.api;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamAdminQueryService;
import com.ops.ops.modules.team.application.dto.response.TeamLikeRankingResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
import com.ops.ops.modules.team.application.dto.response.TeamVoteRateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team Admin", description = "팀 관리 기능 (관리자 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Secured("ROLE_관리자")
public class TeamAdminController {

	private final TeamAdminQueryService teamAdminQueryService;

	@Operation(summary = "전체 팀 등록 현황 조회", description = "관리자가 모든 팀의 제출 여부를 포함한 현황을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/dashboard")
	public ResponseEntity<List<TeamSubmissionStatusResponse>> getAllTeamSubmissions(@LoginMember Member member) {
		return ResponseEntity.ok(teamAdminQueryService.getAllTeamSubmissions());
	}

	@Operation(summary = "좋아요 랭킹 조회", description = "좋아요 수 기준으로 팀 랭킹을 조회합니다. (Competition Ranking 방식)")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/ranking")
	public ResponseEntity<List<TeamLikeRankingResponse>> getTeamLikeRanking(@LoginMember Member member) {
		return ResponseEntity.ok(teamAdminQueryService.getTeamLikeRanking());
	}

	@Operation(summary = "투표 참여율 조회", description = "전체 팀의 좋아요 수를 기반으로 투표 참여율을 계산하여 반환합니다.")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/participation-rate")
	public ResponseEntity<TeamVoteRateResponse> getTeamParticipationRate(@LoginMember Member member) {
		return ResponseEntity.ok(teamAdminQueryService.getVoteRate());
	}
}
