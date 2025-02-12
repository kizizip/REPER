package com.d109.reper.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeSearchRepository extends ElasticsearchRepository<NoticeDocument, Long> {

    //제목으로 공지 검색 및 최근순 정렬
    List<NoticeDocument> findByStoreIdAndTitleContainingOrderByUpdatedAtDesc(Long storeId, String keyword, Pageable pageable);

    //공지 내용으로 검색 및 최근순 정렬
    List<NoticeDocument> findByStoreIdAndContentContainingOrderByUpdatedAtDesc(Long storedId, String keyword, Pageable pageable);

    // noticeId에 맞는 공지 찾기
    Optional<NoticeDocument> findByNoticeId(Long noticeId);
}
