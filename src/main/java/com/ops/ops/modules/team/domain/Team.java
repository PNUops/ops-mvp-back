package com.ops.ops.modules.team.domain;

import com.ops.ops.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

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
    private String youTubePath;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Boolean isSubmitted;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Builder
    public Team(final String leaderName, final String teamName, final String projectName, final String overview,
                final String githubPath, final String youTubePath, final List<TeamMember> teamMembers) {
        this.leaderName = leaderName;
        this.teamName = teamName;
        this.projectName = projectName;
        this.overview = overview;
        this.githubPath = githubPath;
        this.youTubePath = youTubePath;
        this.isDeleted = false;
        this.isSubmitted = false;
        this.teamMembers = teamMembers;
    }

    public void updateDetail(final String newOverview, final String newGithubPath, final String newYouTubePath) {
        this.overview = newOverview;
        this.githubPath = newGithubPath;
        this.youTubePath = newYouTubePath;
        this.isSubmitted = true;
    }

    public boolean isTeamLeader(final Member member) {
        return member != null && this.leaderName.equals(member.getName());
    }
}
