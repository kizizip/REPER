package com.d109.reper.repository;

import com.d109.reper.domain.Notice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

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
    public List<Notice> findAll(Long storeId) {
        return em.createQuery("select n from Notice n where n.store.id = :storeId", Notice.class)
                .setParameter("storeId", storeId)
                .getResultList();
    }

}
