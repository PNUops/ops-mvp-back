package com.ops.ops.modules.team.application.convenience;

import static com.ops.ops.modules.team.exception.TeamCommentExceptionType.NOT_FOUND_COMMENT;

import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.dao.TeamCommentRepository;
import com.ops.ops.modules.team.exception.TeamCommentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamCommentConvenience {

    private final TeamCommentRepository teamCommentRepository;

    public TeamComment getValidateExistComment(final Long commentId) {
        return teamCommentRepository.findById(commentId).orElseThrow(() -> new TeamCommentException(NOT_FOUND_COMMENT));
    }
}
