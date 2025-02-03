package com.d109.reper.service;

import com.d109.reper.domain.Store;
import com.d109.reper.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public Store findById(Long storeId) {
        return storeRepository.findOne(storeId);
    }
}
