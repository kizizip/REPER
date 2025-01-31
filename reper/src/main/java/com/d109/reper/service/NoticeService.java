package com.d109.reper.service;

import com.d109.reper.domain.Notice;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.repository.NoticeRepository;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    // 공지 등록
    @Transactional
    public void saveNotice(Long userId, Long storeId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        // 사장님 본인 매장인지 user_id 확인
        if (!store.getOwner().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("User is not the owner of this store");
        }

        Notice notice = new Notice();
        notice.setStore(store);
        notice.setUser(user);
        notice.setTitle(title);
        notice.setContent(content);
        noticeRepository.save(notice);
    }
}
