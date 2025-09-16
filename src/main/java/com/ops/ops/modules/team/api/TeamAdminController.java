package com.ops.ops.modules.team.api;

import com.ops.ops.modules.team.application.TeamAdminCommandService;
import com.ops.ops.modules.team.application.TeamAdminQueryService;
import com.ops.ops.modules.team.application.dto.request.TeamAwardRequest;
import com.ops.ops.modules.team.application.dto.response.TeamLikeRankingResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
import com.ops.ops.modules.team.application.dto.response.TeamVoteRateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Team Admin", description = "팀 관리 기능 (관리자 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Secured("ROLE_관리자")
public class TeamAdminController {

    private final TeamAdminQueryService teamAdminQueryService;
    private final TeamAdminCommandService teamAdminCommandService;

    @Operation(summary = "전체 팀 등록 현황 조회", description = "관리자가 모든 팀의 제출 여부를 포함한 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/dashboard")
    public ResponseEntity<List<TeamSubmissionStatusResponse>> getAllTeamSubmissions() {
        return ResponseEntity.ok(teamAdminQueryService.getAllTeamSubmissions());
    }

    @Operation(summary = "좋아요 랭킹 조회", description = "좋아요 수 기준으로 팀 랭킹을 조회합니다. (Competition Ranking 방식)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/ranking")
    public ResponseEntity<List<TeamLikeRankingResponse>> getTeamLikeRanking() {
        return ResponseEntity.ok(teamAdminQueryService.getTeamLikeRanking());
    }

    @Operation(summary = "투표 참여율 조회", description = "전체 팀의 좋아요 수를 기반으로 투표 참여율을 계산하여 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/participation-rate")
    public ResponseEntity<TeamVoteRateResponse> getTeamParticipationRate() {
        return ResponseEntity.ok(teamAdminQueryService.getVoteRate());
    }

    @Operation(summary = "팀 수상 설정", description = "관리자가 특정 팀의 상훈명을 등록합니다.")
    @ApiResponse(responseCode = "204", description = "팀 수상 설정 성공")
    @PatchMapping("/teams/{teamId}/award")
    public ResponseEntity<Void> updateTeamAward(@PathVariable final Long teamId,
                                                @Valid @RequestBody final TeamAwardRequest teamAwardRequest) {
        teamAdminCommandService.updateAward(teamId, teamAwardRequest.awardName(), teamAwardRequest.awardColor());
        return ResponseEntity.noContent().build();
    }
}
