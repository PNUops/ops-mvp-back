package com.ops.ops.modules.contest.application;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.ContestTeam;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.domain.dao.ContestTeamRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.team.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestTeamCommandService {
    private final ContestTeamRepository contestTeamRepository;
    private final ContestRepository contestRepository;

    public void ValidateAndUpdateContest(Long teamId, Long newContestId, Team team, Member member, String newTeamName,
                                         String newProjectName, String newLeaderName) {
        ContestTeam contestTeam = contestTeamRepository.findByTeamId(teamId);
        Contest beforeContest = contestTeam.getContest();
        boolean isCurrent = beforeContest.getIsCurrent();

        // 1. 존재하지 않는 대회 id
        Contest newContest = contestRepository.findById(newContestId)
                .orElseThrow(() -> new ContestException(ContestExceptionType.NOT_FOUND_CONTEST));

        // 2. 수정 전 팀의 대회가 isCurrent=true인데 contestId, teamName, projectName, leaderName 중 하나라도 수정하려고 하면 예외
        if (isCurrent) {
            if (contestTeam.isContestChanged(newContestId)) {
                throw new ContestException(ContestExceptionType.CANNOT_CHANGE_CONTEST_FOR_CURRENT);
            }
            if (team.isTeamNameChanged(newTeamName) || team.isProjectNameChanged(newProjectName)
                    || team.isLeaderNameChanged(newLeaderName)) {
                throw new ContestException(ContestExceptionType.CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT);
            }
        }

        // 3. 수정 전 팀의 대회가 isCurrent=false인데 사용자가 관리자 권한이 아니면 예외
        if (!isCurrent && (member == null || member.getRoles().stream().noneMatch(r -> r == MemberRoleType.ROLE_관리자))) {
            throw new ContestException(ContestExceptionType.ADMIN_ONLY_FOR_PAST_CONTEST);
        }

        // 4. 수정 전 팀의 대회가 isCurrent=false인데 사용자가 isCurrent=true인 대회로 변경하려고 함
        if (!isCurrent && newContest.getIsCurrent()) {
            throw new ContestException(ContestExceptionType.CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST);
        }

        // 5. 대회 변경 시 실제 변경
        if (contestTeam.isContestChanged(newContestId)) {
            contestTeamRepository.save(new ContestTeam(teamId, newContest));
        }
    }
}
