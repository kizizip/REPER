package com.d109.reper.service;

import com.d109.reper.domain.Notice;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.repository.NoticeRepository;
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

    // 공지 단건 조회
    public Notice findOneNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("NoticeNotFound"));
    }

    // 매장별 전체 공지 조회
    public List<Notice> findNotices(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        return noticeRepository.findAllByStore_StoreId(storeId);
    }

    // 공지 삭제
    @Transactional
    public void deleteNotice(Long noticeId, Long userId, Long storeId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("NoticeNotFound"));
        // 공지 매장의 owner의 userId가 로그인한 userId랑 다를때
        if (!notice.getStore().getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지 삭제 권한이 없습니다.");
        }

        noticeRepository.delete(notice);
    }
}
