package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {

    private final FileRepository fileRepository;
    private final FileStorageUtil fileStorageUtil;
    private final TeamRepository teamRepository;

    public Pair<Resource, String> findThumbnailImage(final Long teamId) {
        validateAndGetTeamById(teamId);
        final File findFile = fileRepository.findByTeamIdAndType(teamId, THUMBNAIL).orElseThrow(() -> new FileException(
                FileExceptionType.NOT_EXISTS_THUMBNAIL));
        return fileStorageUtil.findFileAndType(findFile.getId());
    }

    public Team validateAndGetTeamById(final Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }
}
