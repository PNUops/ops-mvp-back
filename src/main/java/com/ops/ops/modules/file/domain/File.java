package com.ops.ops.modules.file.domain;

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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileImageType type;

    @Column(nullable = false)
    private boolean isWebpConverted = false;

    @Builder
    private File(final String name, final String filePath, final Long teamId, final FileImageType type) {
        this.name = name;
        this.filePath = filePath;
        this.teamId = teamId;
        this.type = type;
    }

    public void updateIsWebpConverted(boolean isWebpConverted) {
        this.isWebpConverted = isWebpConverted;
    }
}
