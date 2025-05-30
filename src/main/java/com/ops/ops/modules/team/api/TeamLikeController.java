package com.ops.ops.modules.team.api;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamLikeCommandService;
import com.ops.ops.modules.team.application.dto.request.TeamLikeToggleRequest;
import com.ops.ops.modules.team.application.dto.response.TeamLikeToggleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team Like", description = "팀 좋아요 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamLikeController {

    private final TeamLikeCommandService teamLikeService;

    @Operation(summary = "좋아요 토글", description = "특정 팀에 대해 좋아요를 등록하거나 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 상태 변경 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 팀 ID")
    })
    @PatchMapping("/{teamId}/like")
    public ResponseEntity<TeamLikeToggleResponse> toggleLike(
            @PathVariable Long teamId,
            @RequestBody TeamLikeToggleRequest request,
            @LoginMember Member member) {
        TeamLikeToggleResponse response =
                teamLikeService.toggleLike(member.getId(), teamId, request.isLiked());
        return ResponseEntity.ok(response);
    }
}
