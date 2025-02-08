package com.d109.reper.service;

import com.d109.reper.controller.NoticeController;
import com.d109.reper.domain.Notice;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.User;
import com.d109.reper.elasticsearch.NoticeDocument;
import com.d109.reper.elasticsearch.NoticeSearchRepository;
import com.d109.reper.repository.NoticeRepository;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;
    private final NoticeSearchRepository noticeSearchRepository;


    // 공지 등록
    @Transactional
    public Notice saveNotice(Long storeId, Long userId, String title, String content) {
        if (storeId == null || storeId <=0 || userId == null || userId <= 0) {
            throw new IllegalArgumentException("storeId, userId는 필수이고, 1이상의 값이어야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store Not Found"));

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

        // 엘라스틱서치에 인덱싱
        NoticeDocument noticeDocument = new NoticeDocument();
        noticeDocument.setNoticeId(saveNotice.getNoticeId());
        noticeDocument.setStoreId(storeId);
        noticeDocument.setTitle(title);
        noticeDocument.setContent(content);
        noticeDocument.setUpdatedAt(saveNotice.getUpdatedAt());

        noticeSearchRepository.save(noticeDocument);

        if (saveNotice == null) {
            throw new RuntimeException("Notice 등록 실패: save() 결과가 null입니다.");
        }

        if (saveNotice.getNoticeId() == null) {
            throw new RuntimeException("Notice 등록 실패: ID가 생성되지 않았습니다.");
        }

        return saveNotice;
    }


    // 공지 단건 조회
    public Notice findOneNotice(Long noticeId, Long storeId, Long userId) {
        if (noticeId == null || storeId ==null || userId ==null) {
            throw new IllegalArgumentException("noticeId, storeId, userId는 필수입니다.");
        }
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice Not Found"));

        if (!notice.getStore().getStoreId().equals(storeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 공지는 해당 매장에 속하지 않습니다.");
        }

        if (!isAuthorizedUser(store, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        return notice;
    }


    // 매장별 전체 공지 조회
    public List<NoticeController.ResponseNotices> findNotices(Long storeId, Long userId) {
        if (storeId == null || userId ==null) {
            throw new IllegalArgumentException("storeId, userId는 필수입니다.");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        User user = userRepository.findById(userId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!isAuthorizedUser(store, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        List<Notice> notices = noticeRepository.findAllByStore_StoreId(storeId);
        LocalDateTime now = LocalDateTime.now();

        List<NoticeController.ResponseNotices.NoticeResponse> sortedNotices = notices.stream()
                .sorted(Comparator.comparing(Notice::getUpdatedAt).reversed()) // 최신순 정렬
                .map(notice -> new NoticeController.ResponseNotices.NoticeResponse(
                        notice.getNoticeId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getUpdatedAt(),
                        formatTimeAgo(notice.getUpdatedAt(), now) // timeAgo 필드 추가
                ))
                .collect(Collectors.toList());

        return List.of(new NoticeController.ResponseNotices(storeId, sortedNotices));
    }


    // 공지 수정
    @Transactional
    public NoticeController.ResponseNoticeSave updateNotice(Long noticeId, Long storeId, Long userId, String newTitle, String newContent) {
        if (noticeId == null || storeId == null || userId ==null) {
            throw new IllegalArgumentException("noticeId, storeId, userId는 필수입니다.");
        }

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice Not Found"));

        NoticeDocument elasticNotice = noticeSearchRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Elasticsearch에 해당 공지가 없습니다."));

        storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store Not Found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

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
            elasticNotice.setTitle(newTitle);
            isUpdated = true;
        }

        if (newContent != null && !newContent.equals(notice.getContent())) {
            notice.setContent(newContent);
            elasticNotice.setContent(newContent);
            isUpdated = true;
        }

        // 변경된 내용이 있으면 updatedAt 갱신
        if (isUpdated) {
            notice.setUpdatedAt(LocalDateTime.now());
            elasticNotice.setUpdatedAt(LocalDateTime.now());
            noticeSearchRepository.save(elasticNotice);
        }

        String message = isUpdated ? "공지 수정 완료" : "변경된 내용이 없습니다.";

        return new NoticeController.ResponseNoticeSave(
                message,
                notice.getNoticeId(),
                notice.getStore().getStoreId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getUpdatedAt());
    }

    // 공지 삭제
    @Transactional
    public void deleteNotice(Long noticeId, Long storeId, Long userId) {
        if (noticeId == null || storeId == null || userId ==null) {
            throw new IllegalArgumentException("noticeId, storeId, userId는 필수입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store Not Found"));

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice Not Found"));

        NoticeDocument elasticNotice = noticeSearchRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Elasticsearch에 해당 공지가 없습니다."));

        if (!notice.getStore().getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지 삭제 권한이 없습니다.");
        }

        if (!notice.getStore().getStoreId().equals(store.getStoreId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 가게에 속한 공지가 아닙니다.");
        }
        noticeRepository.delete(notice);
        noticeSearchRepository.delete(elasticNotice);
    }


    // Elasticsearch에서 공지 제목 검색
    public List<NoticeDocument> searchNoticesTitle(Long storeId, String keyword) {

        if (keyword == null) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        Pageable pageable = PageRequest.of(0, 1000);

        List<NoticeDocument> notices = noticeSearchRepository
                .findByStoreIdAndTitleContainingOrderByUpdatedAtDesc(storeId, keyword, pageable);

        LocalDateTime now = LocalDateTime.now();

        for (NoticeDocument notice : notices) {
            notice.setTimeAgo(formatTimeAgo(notice.getUpdatedAt(), now));
        }

        return notices;
    }

    // Elasticsearch에서 공지 내용 검색
    public List<NoticeDocument> searchNoticesContent(Long storeId, String keyword) {

        if (keyword == null) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        Pageable pageable = PageRequest.of(0, 1000);

        List<NoticeDocument> notices = noticeSearchRepository
                .findByStoreIdAndContentContainingOrderByUpdatedAtDesc(storeId, keyword, pageable);

        LocalDateTime now = LocalDateTime.now();

        for (NoticeDocument notice : notices) {
            notice.setTimeAgo(formatTimeAgo(notice.getUpdatedAt(), now));
        }

        return notices;
    }


    // Elasticsearch test위한 더미 데이터 동기화용
    @Transactional
    public void syncNoticesToElasticsearch() {
        List<Notice> allNotices = noticeRepository.findAll();  // MySQL에서 모든 공지 가져오기

        for (Notice notice : allNotices) {
            NoticeDocument noticeDocument = new NoticeDocument();
            noticeDocument.setNoticeId(notice.getNoticeId());
            noticeDocument.setStoreId(notice.getStore().getStoreId());
            noticeDocument.setTitle(notice.getTitle());
            noticeDocument.setContent(notice.getContent());
            noticeDocument.setUpdatedAt(notice.getUpdatedAt());

            noticeSearchRepository.save(noticeDocument);
        }
    }


    // 사용자가 해당 매장과 관련이 있는지 검증 로직
    private boolean isAuthorizedUser(Store store, User user) {
        // 사장인지 확인
        if (store.getOwner().equals(user)) {
            return true;
        }
        // 알바생인지 확인
        return storeEmployeeRepository.existsByUser_UserIdAndStore_StoreIdAndIsEmployedTrue(user.getUserId(), store.getStoreId());

    }

    // 전체 공지 조회시 updatedAt 최신순으로 정렬 및 timeAgo 계산 로직
    private String formatTimeAgo(LocalDateTime updatedAt, LocalDateTime now) {
        Duration duration = Duration.between(updatedAt, now);
        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (seconds < 60) {
            return seconds + "초 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }

}
