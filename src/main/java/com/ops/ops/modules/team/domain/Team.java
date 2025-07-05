package com.ops.ops.modules.team.domain;

import com.ops.ops.global.base.BaseEntity;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE team SET is_deleted = true where id = ?")
public class Team extends BaseEntity {
    // 이미지 관련 추가 필요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String leaderName;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String projectName;

    @Column
    private String overview;

    @Column
    private String githubPath;

    @Column
    private String productionPath;

    @Column
    private String youTubePath;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Boolean isSubmitted;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Column(nullable = false)
    private Long contestId;

    @Builder
    public Team(final String leaderName, final String teamName, final String projectName, final String overview,
                final String productionPath, final String githubPath, final String youTubePath,
                final List<TeamMember> teamMembers, final Long contestId) {
        this.leaderName = leaderName;
        this.teamName = teamName;
        this.projectName = projectName;
        this.overview = overview;
        this.productionPath = productionPath;
        this.githubPath = githubPath;
        this.youTubePath = youTubePath;
        this.isDeleted = false;
        this.isSubmitted = false;
        this.teamMembers = teamMembers;
        this.contestId = contestId;
    }

    public static Team of(String leaderName, String teamName, String projectName, String overview,
                          String productionPath, String githubPath, String youTubePath, final Long contestId) {
        return Team.builder()
                .leaderName(leaderName)
                .teamName(teamName)
                .projectName(projectName)
                .overview(overview)
                .productionPath(productionPath)
                .githubPath(githubPath)
                .youTubePath(youTubePath)
                .teamMembers(new ArrayList<>())
                .contestId(contestId)
                .build();
    }

    public void updateDetail(final String newleaderName, final String newTeamName, final String newProjectName,
                             final String newOverview,
                             final String newProductionPath, final String newGithubPath, final String newYouTubePath,
                             final Long newContestId) {
        this.leaderName = newleaderName;
        this.teamName = newTeamName;
        this.projectName = newProjectName;
        this.overview = newOverview;
        this.productionPath = newProductionPath;
        this.githubPath = newGithubPath;
        this.youTubePath = newYouTubePath;
        this.isSubmitted = true;
        this.contestId = newContestId;
    }

    public boolean isContestChanged(Long newContestId) {
        return !this.contestId.equals(newContestId);
    }

    public boolean isTeamInfoChanged(String newTeamName, String newProjectName, String newLeaderName) {
        return !this.teamName.equals(newTeamName) || !this.projectName.equals(newProjectName)
                || !this.leaderName.equals(newLeaderName);
    }

    public boolean isLeaderNameChanged(String newLeaderName) {
        return !this.getLeaderName().equals(newLeaderName);
    }

    public TeamMember addTeamMember(Long memberId) {
        TeamMember newLeader = TeamMember.builder()
                .memberId(memberId)
                .team(this)
                .build();
        this.teamMembers.add(newLeader);
        return newLeader;
    }

    public TeamMember findTeamMemberByName(String memberName, MemberRepository memberRepository) {
        return this.teamMembers.stream()
                .filter(tm -> !tm.getIsDeleted())
                .filter(tm -> memberRepository.findById(tm.getMemberId())
                        .map(Member::getName)
                        .map(name -> name.equals(memberName))
                        .orElse(false))
                .findFirst()
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER));
    }
}
