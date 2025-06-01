package com.ops.ops.modules.file.domain.dao;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByTeamIdAndType(Long teamId, FileImageType type);
    File findByTeamIdandType(Long teamId, FileImageType fileImageType);
}
