package com.d109.reper.repository;

import com.d109.reper.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 특정 매장의 모든 공지 조회
    List<Notice> findAllByStore_StoreId(Long storeId);
}
