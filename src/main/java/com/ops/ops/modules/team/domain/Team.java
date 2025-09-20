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

    private static final int MAX_OVERVIEW_LENGTH = 3000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String leaderName;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String projectName;

    @Column(length = MAX_OVERVIEW_LENGTH)
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

    @Column
    private String awardName;

    @Column
    private String awardColor;

    @Column(nullable = false)
    private Integer itemOrder;

    @Column
    private String professorName;

    @Builder
    public Team(final String leaderName, final String teamName, final String projectName, final String overview,
                final String productionPath, final String githubPath, final String youTubePath, final Long contestId,
                final Integer itemOrder, final String professorName) {
        this.leaderName = leaderName;
        this.teamName = teamName;
        this.projectName = projectName;
        this.overview = overview;
        this.productionPath = productionPath;
        this.githubPath = githubPath;
        this.youTubePath = youTubePath;
        this.isDeleted = false;
        this.isSubmitted = false;
        this.teamMembers = new ArrayList<>();
        this.contestId = contestId;
        this.awardName = null;
        this.awardColor = null;
        this.itemOrder = itemOrder;
        this.professorName = professorName;
    }

    public void updateDetail(final String newLeaderName, final String newTeamName, final String newProjectName,
                             final String newOverview, final String newProductionPath, final String newGithubPath,
                             final String newYouTubePath, final Long newContestId, final String newpPofessorName) {
        this.leaderName = newLeaderName;
        this.teamName = newTeamName;
        this.projectName = newProjectName;
        this.overview = newOverview;
        this.productionPath = newProductionPath;
        this.githubPath = newGithubPath;
        this.youTubePath = newYouTubePath;
        this.isSubmitted = true;
        this.contestId = newContestId;
        this.professorName = newpPofessorName;
    }

    public void updateAward(final String awardName, final String awardColor) {
        this.awardName = awardName;
        this.awardColor = awardColor;
    }

    public boolean isContestChanged(Long newContestId) {
        return !this.contestId.equals(newContestId);
    }

    public boolean isTeamInfoChanged(String newTeamName, String newLeaderName) {
        return !this.teamName.equals(newTeamName) || !this.leaderName.equals(newLeaderName);
    }

    public boolean isLeaderNameChanged(String newLeaderName) {
        return !this.getLeaderName().equals(newLeaderName);
    }

    public void updateItemOrder(Integer newOrder) {
        this.itemOrder = newOrder;
    }
}
