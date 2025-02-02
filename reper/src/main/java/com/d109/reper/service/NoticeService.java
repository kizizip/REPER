package com.d109.reper.service;

import com.d109.reper.controller.NoticeController;
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
    public Long saveNotice(Long storeId, Long userId, String title, String content) {
        if (storeId == null || storeId <=0 || userId == null || userId <= 0) {
            throw new IllegalArgumentException("storeId, userId는 필수이고, 1이상의 값이어야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        if (!store.getOwner().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("User is not the owner of this store");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        Notice notice = new Notice();
        notice.setStore(store);
        notice.setUser(user);
        notice.setTitle(title);
        notice.setContent(content);
        Notice saveNotice = noticeRepository.save(notice);

        if (saveNotice == null) {
            throw new RuntimeException("Notice 등록 실패: save() 결과가 null입니다.");
        }

        if (saveNotice.getNoticeId() == null) {
            throw new RuntimeException("Notice 등록 실패: ID가 생성되지 않았습니다.");
        }

        return saveNotice.getNoticeId();
    }


    // 공지 단건 조회
    public Notice findOneNotice(Long noticeId) {
        // 내가 다니는(알바생), 내 매장(사장)이 아닌 공지 조회 못하게 - ing
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("NoticeNotFound"));
    }


    // 매장별 전체 공지 조회
    public List<Notice> findNotices(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
        // 매장은 등록되어있는데 공지가 하나도 없으면? 빈값가나? 오류 안나나?
        // 내가 다니는(알바생), 내 매장(사장)이 아닌 공지 조회 못하게 - ing

        return noticeRepository.findAllByStore_StoreId(storeId);
    }


    // 공지 수정
    @Transactional
    public NoticeController.ResponseBody updateNotice(Long noticeId, Long storeId, Long userId, String newTitle, String newContent) {
        if (noticeId == null || storeId == null || userId ==null) {
            throw new IllegalArgumentException("noticeId, storeId, userId는 필수입니다.");
        }

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("NoticeNotFound"));

        storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        if (!notice.getStore().getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("해당 가게에 속한 공지가 아닙니다.");
        }

        if (!notice.getStore().getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지 수정 권한이 없습니다.");
        }

        if (newTitle != null && newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (newContent != null && newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        boolean isUpdated = false;

        if (newTitle != null && !newTitle.equals(notice.getTitle())) {
            notice.setTitle(newTitle);
            isUpdated = true;
        }

        if (newContent != null && !newContent.equals(notice.getContent())) {
            notice.setContent(newContent);
            isUpdated = true;
        }

        String message = isUpdated ? "공지 수정 완료" : "변경된 내용이 없습니다.";

        return new NoticeController.ResponseBody(
                message,
                notice.getNoticeId(),
                notice.getStore().getStoreId(),
                userId,
                notice.getTitle(),
                notice.getContent());
    }

    // 공지 삭제
    @Transactional
    public void deleteNotice(Long noticeId, Long storeId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("NoticeNotFound"));
        // 공지 매장의 owner의 userId가 로그인한 userId랑 다를때 - 작동 잘함
        if (!notice.getStore().getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지 삭제 권한이 없습니다.");
        }
        // 뭔가 예외처리 되어서 안되야 하는데 삭제 됨.코드 수정했으니 다시 확인해보기
            // storedId 랑 noticeId.storeId랑 다른데 삭제 됨 예외처리 하기
        noticeRepository.delete(notice);
    }


}
