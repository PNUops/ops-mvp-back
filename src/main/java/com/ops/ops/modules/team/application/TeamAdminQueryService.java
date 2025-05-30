package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamAdminQueryService {

	private final TeamRepository teamRepository;

	public List<TeamSubmissionStatusResponse> getAllTeamSubmissions() {
		return teamRepository.findAll()
			.stream()
			.map(TeamSubmissionStatusResponse::fromEntity)
			.collect(Collectors.toList());
	}
}
