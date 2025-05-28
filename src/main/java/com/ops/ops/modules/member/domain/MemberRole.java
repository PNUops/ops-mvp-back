package com.ops.ops.modules.member.domain;

import com.ops.ops.global.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@SQLDelete(sql = "UPDATE member_role SET is_deleted = true where id = ?")
public class MemberRole extends BaseEntity {

    private static final int MAX_ROLE_NAME_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false, length = MAX_ROLE_NAME_LENGTH)
    private MemberRoleType memberRoleType;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    private MemberRole(final MemberRoleType memberRoleType) {
        this.memberRoleType = memberRoleType;
        this.isDeleted = false;
    }
}
