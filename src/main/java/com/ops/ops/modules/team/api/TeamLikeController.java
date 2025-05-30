package com.ops.ops.modules.team.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamLikeCommandService;
import com.ops.ops.modules.team.application.dto.request.TeamLikeToggleRequest;
import com.ops.ops.modules.team.application.dto.response.TeamLikeToggleResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamLikeController {

	private final TeamLikeCommandService teamLikeService;

	@PatchMapping("/{teamId}/like")
	public ResponseEntity<TeamLikeToggleResponse> toggleLike(
		@PathVariable Long teamId,
		@RequestBody TeamLikeToggleRequest request,
		@LoginMember Member member
	) {
		TeamLikeToggleResponse response = teamLikeService.toggleLike(member.getId(), teamId, request.isLiked());
		return ResponseEntity.ok(response);
	}
}
