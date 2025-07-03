package com.ops.ops.modules.contest.domain;

import com.ops.ops.global.base.BaseEntity;
import com.ops.ops.modules.team.domain.Team;
import jakarta.persistence.CascadeType;
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
@SQLDelete(sql = "UPDATE contest SET is_deleted = true where id = ?")
public class Contest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contestName;

    @Column(nullable = false)
    private Boolean isCurrent;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    @Builder
    public Contest(final String contestName, final Boolean isCurrent, final List<Team> teams) {
        this.contestName = contestName;
        this.isDeleted = false;
        this.isCurrent = isCurrent;
        this.teams = teams;
    }

    public void updateContestName(final String newContestName) {
        this.contestName = newContestName;
    }
}
