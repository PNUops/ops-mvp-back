package com.ops.ops.modules.team.domain.dao;

import com.ops.ops.modules.team.domain.TeamComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeamCommentRepository extends JpaRepository<TeamComment, Long> {
    List<TeamComment> findAllByTeamIdOrderByIdDesc(Long id);

    @Modifying
    @Query("DELETE FROM TeamComment tc WHERE tc.team.id = :teamId")
    void deleteAllByTeamId(Long teamId);
}
