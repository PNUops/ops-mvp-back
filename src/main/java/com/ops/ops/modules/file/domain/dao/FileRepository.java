package com.ops.ops.modules.file.domain.dao;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByTeamIdAndType(Long teamId, FileImageType type);

    Optional<File> findByTeamIdAndType(Long teamId, FileImageType type);
    
    long countByTeamIdAndType(Long teamId, FileImageType type);
}
