package com.d109.reper.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, Long> {

    List<StoreDocument> findByStoreNameContaining(String storeName, Pageable pageable);

    Optional<StoreDocument> findByStoreId(Long storeId);
}
