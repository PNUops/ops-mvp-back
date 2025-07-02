package com.ops.ops.modules.contest.domain;

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

    @OneToMany(mappedBy = "contest")
    private List<ContestTeam> contestTeams = new ArrayList<>();

    @Builder
    public Contest(final String contestName, final Boolean isCurrent, final List<ContestTeam> contestTeams) {
        this.contestName = contestName;
        this.isDeleted = false;
        this.isCurrent = isCurrent;
        this.contestTeams = contestTeams;
    }

    public static Contest of(final String contestName, final Boolean isCurrent) {
        return Contest.builder()
                .contestName(contestName)
                .isCurrent(isCurrent)
                .build();
    }

    public void updateContestName(final String newContestName) {
        this.contestName = newContestName;
    }
}
