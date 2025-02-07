package com.d109.reper.service;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.elasticsearch.StoreDocument;
import com.d109.reper.elasticsearch.StoreSearchRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreSearchRepository storeSearchRepository;

    // 사장님이 가진 모든 매장 조회
    public List<Store> findOwnerStores(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        if (!user.getRole().equals(UserRole.OWNER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 user는 사장님이 아닙니다.");
        }

        return storeRepository.findAllByOwner(user);
    }

    // Elasticsearch 매장 이름 검색
    public List<StoreDocument> searchStoreByName(String keyword) {

        if (keyword == null) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        return storeSearchRepository.findByStoreNameContaining(keyword);
    }

    // Elasticsearch 매장 이름 검색 더미 데이터 test용 로직
    @Transactional
    public void syncStoresToElasticsearch() {
        List<Store> stores = storeRepository.findAll();

        List<StoreDocument> storeDocuments = stores.stream()
                .map(store -> {
                    StoreDocument doc = new StoreDocument();
                    doc.setStoreId(store.getStoreId());
                    doc.setStoreName(store.getStoreName());
                    return doc;
                })
                .toList();

        storeSearchRepository.saveAll(storeDocuments);
    }

}
