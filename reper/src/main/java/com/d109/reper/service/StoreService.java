package com.d109.reper.service;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.elasticsearch.StoreDocument;
import com.d109.reper.elasticsearch.StoreSearchRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import com.d109.reper.request.StoreRequestDto;
import com.d109.reper.response.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreSearchRepository storeSearchRepository;

    // 매장 정보 등록
    @Transactional
    public StoreResponseDto createStore(StoreRequestDto storeRequestDto) {

        User owner = userRepository.findById(storeRequestDto.getOwnerId()).orElseThrow();

        if (isStoreExists(storeRequestDto.getStoreName(), owner)) {
            throw new IllegalStateException("The store already exists.");
        }

        if (owner.getRole() != UserRole.OWNER) {
            throw new AccessDeniedException("Only users with role 'owner' can register a store.");
        }

        Store store = new Store();

        store.setStoreName(storeRequestDto.getStoreName());
        store.setOwner(owner);

        Store savedStore = storeRepository.save(store);
        return new StoreResponseDto(savedStore);
    }

    // 존재하는 가게인지 확인(가게이름과 사장님으로)
    public boolean isStoreExists(String storeName, User owner) {
        return storeRepository.existsByStoreNameAndOwner(storeName, owner);
    }


    // 매장 검색

    // 매장 정보 조회(단건)
    public StoreResponseDto getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found."));
        return new StoreResponseDto(store);
    }


    // 매장 정보 삭제
    @Transactional
    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found"));

        storeRepository.delete(store);
    }


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
