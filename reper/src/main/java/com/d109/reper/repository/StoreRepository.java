package com.d109.reper.repository;

import com.d109.reper.domain.Store;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepository {

    private final EntityManager em;

    public Store findOne(Long id) {
        return em.find(Store.class, id);
    }
}
