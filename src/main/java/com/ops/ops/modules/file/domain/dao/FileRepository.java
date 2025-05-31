package com.ops.ops.modules.file.domain.dao;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByTeamIdAndType(Long teamId, FileImageType type);
}
