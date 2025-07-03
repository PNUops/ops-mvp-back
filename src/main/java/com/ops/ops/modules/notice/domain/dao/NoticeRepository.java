package com.ops.ops.modules.notice.domain.dao;

import com.ops.ops.modules.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
