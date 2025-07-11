package com.ops.ops.modules.contest.api;

import static org.springframework.http.HttpStatus.CREATED;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.contest.application.ContestCommandService;
import com.ops.ops.modules.contest.application.ContestQueryService;
import com.ops.ops.modules.contest.application.dto.request.ContestRequest;
import com.ops.ops.modules.contest.application.dto.response.ContestResponse;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contest", description = "대회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/contests")
public class ContestController {
    private final ContestQueryService contestQueryService;
    private final ContestCommandService contestCommandService;

    @Operation(summary = "모든 대회 조회", description = "등록된 모든 대회를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 대회 조회 성공")
    @GetMapping
    public ResponseEntity<List<ContestResponse>> getAllContests() {
        List<ContestResponse> responses = contestQueryService.getAllContests();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "대회 등록", description = "새로운 대회를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "대회 등록 성공")
    @PostMapping
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> createContest(
            @Valid @RequestBody final ContestRequest request
    ) {
        contestCommandService.createContest(request.contestName());
        return ResponseEntity.status(CREATED).build();
    }

    @Operation(summary = "대회 수정", description = "특정 대회의 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "대회 정보 수정 성공")
    @PatchMapping("/{contestId}")
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> updateContest(
            @PathVariable final Long contestId,
            @Valid @RequestBody final ContestRequest request
    ) {
        contestCommandService.updateContest(contestId, request.contestName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "대회 삭제", description = "특정 대회를 삭제합니다. (소프트 삭제)")
    @ApiResponses(@ApiResponse(responseCode = "204", description = "대회 삭제 성공"))
    @DeleteMapping("/{contestId}")
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> deleteContest(
            @PathVariable final Long contestId
    ) {
        contestCommandService.deleteContest(contestId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 대회 전체 팀 조회", description = "해당 대회의 모든 팀을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "대회의 팀 전체 조회 성공")
    @GetMapping("/{contestId}/teams")
    public ResponseEntity<List<TeamSummaryResponse>> getAllContestTeams(
            @PathVariable final Long contestId,
            @LoginMember final Member member
    ) {
        List<TeamSummaryResponse> responses = contestQueryService.getContestTeamSummaries(contestId, member);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "현재 진행 중인 대회의 전체 팀 조회", description = "현재 진행 중인 대회의 모든 팀을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "현재 진행 중인 대회의 팀 전체 조회 성공")
    @GetMapping("/current/teams")
    public ResponseEntity<List<TeamSummaryResponse>> getAllCurrentContestTeams(
            @LoginMember final Member member
    ) {
        List<TeamSummaryResponse> responses = contestQueryService.getCurrentContestTeamSummaries(member);
        return ResponseEntity.ok(responses);
    }
}
