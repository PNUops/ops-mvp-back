package com.ops.ops.modules.team.api;

import java.util.List;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamCommentCommandService;
import com.ops.ops.modules.team.application.TeamCommentQueryService;
import com.ops.ops.modules.team.application.dto.request.TeamCommentCreateRequest;
import com.ops.ops.modules.team.application.dto.response.TeamCommentResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams/{teamId}/comments")
public class TeamCommentController {

	private final TeamCommentCommandService teamCommentCommandService;
	private final TeamCommentQueryService teamCommentQueryService;

	@PostMapping
	public ResponseEntity<Void> createComment(
		@PathVariable Long teamId,
		@Valid @RequestBody final TeamCommentCreateRequest request,
		@LoginMember final Member member
	) {
		teamCommentCommandService.createComment(teamId, member.getId(), request.description());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<List<TeamCommentResponse>> getComments(
		@PathVariable final Long teamId,
		@LoginMember final Member member
	) {
		List<TeamCommentResponse> response = teamCommentQueryService.getComments(teamId);
		return ResponseEntity.ok(response);
	}
}
