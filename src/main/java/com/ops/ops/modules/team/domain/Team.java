package com.ops.ops.modules.team.domain;

import com.ops.ops.global.base.BaseEntity;
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

    @Builder
    public Team(final String leaderName, final String teamName, final String projectName, final String overview,
                final String productionPath,
                final String githubPath, final String youTubePath, final List<TeamMember> teamMembers) {
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
    }

    public void updateDetail(final String newTeamName, final String newProjectName, final String newLeaderName,
                             final String newOverview, final String newProductionPath, final String newGithubPath,
                             final String newYouTubePath) {
        this.teamName = newTeamName;
        this.projectName = newProjectName;
        this.leaderName = newLeaderName;
        this.overview = newOverview;
        this.productionPath = newProductionPath;
        this.githubPath = newGithubPath;
        this.youTubePath = newYouTubePath;
        this.isSubmitted = true;
    }

    public boolean isTeamNameChanged(String newTeamName) {
        return !this.teamName.equals(newTeamName);
    }

    public boolean isProjectNameChanged(String newProjectName) {
        return !this.projectName.equals(newProjectName);
    }

    public boolean isLeaderNameChanged(String newLeaderName) {
        return !this.leaderName.equals(newLeaderName);
    }

    public TeamMember addTeamMember(Long memberId) {
        TeamMember newLeader = TeamMember.builder()
                .memberId(memberId)
                .team(this)
                .build();
        this.teamMembers.add(newLeader);
        return newLeader;
    }
}
