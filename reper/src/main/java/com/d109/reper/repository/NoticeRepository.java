package com.d109.reper.repository;

import com.d109.reper.domain.Notice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {
    private final EntityManager em;

    //공지 저장
    public void save(Notice notice) {
        em.persist(notice);
    }

    // 공지 단건 조회
    public Notice findOne(Long id) {
        return em.find(Notice.class, id);
    }

    // 공지 전체 조회

}
