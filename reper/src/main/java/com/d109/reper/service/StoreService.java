package com.d109.reper.service;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import com.d109.reper.request.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 매장 정보 등록
    @Transactional
    public Store createStore(StoreRequestDto storeRequestDto) {

        User owner = userRepository.findById(storeRequestDto.getOwnerId()).orElseThrow();

//        if (owner.getRole() != )

        Store store = new Store();

        store.setStoreName(storeRequestDto.getStoreName());
        store.setOwner(owner);
        return storeRepository.save(store);
    }

    // 존재하는 가게인지 확인(가게이름과 사장님으로)
    public boolean isStoreExists(String storeName, User owner) {
        return storeRepository.existsByStoreNameAndOwner(storeName, owner);
    }


    // 매장 검색

    // 매장 정보 조회(단건)

    // 매장 정보 수정

    // 매장 정보 삭제

    // 사장님이 가진 모든 매장 조회
    public List<Store> findStores(Long userId) {
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

}
