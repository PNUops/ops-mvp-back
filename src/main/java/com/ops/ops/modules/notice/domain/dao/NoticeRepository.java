package com.ops.ops.modules.notice.domain.dao;

import com.ops.ops.modules.notice.domain.Notice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByCreatedAtDesc();

}
