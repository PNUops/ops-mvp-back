package com.ops.ops.modules.team.api;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamLikeCommandService;
import com.ops.ops.modules.team.application.dto.request.TeamLikeToggleRequest;
import com.ops.ops.modules.team.application.dto.response.TeamLikeCountResponse;
import com.ops.ops.modules.team.application.dto.response.TeamLikeToggleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Team Like", description = "팀 좋아요 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
@Secured({"ROLE_회원", "ROLE_팀장", "ROLE_관리자", "ROLE_팀원"})
public class TeamLikeController {

    private final TeamLikeCommandService teamLikeService;

    @Operation(summary = "좋아요 토글", description = "특정 팀에 대해 좋아요를 등록하거나 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 상태 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복, 제한 초과, 투표기간이 아님 등)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 팀 ID")
    })
    @PatchMapping("/{teamId}/like")
    public ResponseEntity<TeamLikeToggleResponse> toggleLike(@PathVariable Long teamId,
                                                             @RequestBody @Valid TeamLikeToggleRequest request,
                                                             @LoginMember Member member) {
        return ResponseEntity.ok(teamLikeService.toggleLike(member.getId(), teamId, request.isLiked()));
    }

    @Operation(summary = "사용자의 좋아요 개수 상태 조회", description = "현재 사용자가 특정 대회에서 좋아요를 누른 팀의 개수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 개수 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/likes")
    public ResponseEntity<TeamLikeCountResponse> getUserLikeCount(@RequestParam Long contestId,
                                                                  @LoginMember Member member) {
        TeamLikeCountResponse response = new TeamLikeCountResponse(
                teamLikeService.countCurrentMemberLikes(member.getId(), contestId));
        return ResponseEntity.ok(response);
    }
}
