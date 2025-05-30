package com.ops.ops.modules.file.domain.dao;

import com.ops.ops.modules.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
