package com.d109.reper.service;

import com.d109.reper.domain.Notice;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.repository.NoticeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 공지 단건 조회
    public Notice findOneNotice(Long noticeId) {
        return noticeRepository.findOne(noticeId);
    }

    // 매장별 전체 공지 조회
    public List<Notice> findNotices(Long storeId) {
        //본인이 다니는 매장인지 예외처리 나중에 할까...??

        return noticeRepository.findAll(storeId);
    }


    // 공지 삭제
//    public void deleteNotice(Long noticeId, Long userId, Long storeId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));
//
//        Store store = storeRepository.findById(storeId)
//                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));
//
//        Notice notice = noticeRepository.findOne(noticeId)
//                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));
//
//        // 공지의 store의 owner의 userid와 로그인한사용자 userid가 같지 않으면
//        if (!store.getOwner().getUserId().equals(user.getUserId())) {
//
//        }
//
//        noticeRepository.delete(notice);
//    }
}
