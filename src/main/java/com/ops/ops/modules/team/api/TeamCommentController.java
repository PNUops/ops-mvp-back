package com.ops.ops.modules.team.api;

import java.util.List;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamCommentCommandService;
import com.ops.ops.modules.team.application.TeamCommentQueryService;
import com.ops.ops.modules.team.application.dto.request.TeamCommentCreateRequest;
import com.ops.ops.modules.team.application.dto.request.TeamCommentUpdateRequest;
import com.ops.ops.modules.team.application.dto.response.TeamCommentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team Comment", description = "팀 댓글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams/{teamId}/comments")
@Secured({"ROLE_회원", "ROLE_팀장", "ROLE_관리자"})
public class TeamCommentController {

	private final TeamCommentCommandService teamCommentCommandService;
	private final TeamCommentQueryService teamCommentQueryService;

	@Operation(summary = "댓글 생성", description = "특정 팀에 댓글을 작성합니다.")
	@ApiResponse(responseCode = "201", description = "댓글 생성 성공")
	@PostMapping
	public ResponseEntity<Void> createTeamComment(
		@PathVariable final Long teamId,
		@Valid @RequestBody final TeamCommentCreateRequest request,
		@LoginMember final Member member
	) {
		teamCommentCommandService.createComment(teamId, member.getId(), request.description());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "댓글 목록 조회", description = "특정 팀에 등록된 댓글 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
	@GetMapping
	public ResponseEntity<List<TeamCommentResponse>> getTeamComments(
		@Parameter(description = "팀 ID") @PathVariable final Long teamId
	) {
		List<TeamCommentResponse> response = teamCommentQueryService.getComments(teamId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "댓글 수정", description = "특정 팀의 댓글을 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
		@ApiResponse(responseCode = "403", description = "본인이 작성한 댓글이 아님"),
		@ApiResponse(responseCode = "404", description = "댓글 또는 팀이 존재하지 않음")
	})
	@PatchMapping("/{commentId}")
	public ResponseEntity<Void> updateTeamComment(
		@Parameter(description = "팀 ID") @PathVariable final Long teamId,
		@Parameter(description = "댓글 ID") @PathVariable final Long commentId,
		@Valid @RequestBody final TeamCommentUpdateRequest request,
		@LoginMember final Member member
	) {
		teamCommentCommandService.updateComment(teamId, commentId, member.getId(), request.description());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "댓글 삭제", description = "특정 팀의 댓글을 삭제합니다. (소프트 삭제)")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
		@ApiResponse(responseCode = "403", description = "본인이 작성한 댓글이 아님"),
		@ApiResponse(responseCode = "404", description = "댓글 또는 팀이 존재하지 않음")
	})
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteTeamComment(
		@Parameter(description = "팀 ID") @PathVariable final Long teamId,
		@Parameter(description = "댓글 ID") @PathVariable final Long commentId,
		@LoginMember final Member member
	) {
		teamCommentCommandService.deleteComment(teamId, commentId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
