package com.d109.reper.fcm;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.OrderDetail;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.service.FcmMessageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class OrderEventListener {
    private final FcmMessageService fcmMessageService;

    private final StoreEmployeeRepository storeEmployeeRepository;

    public OrderEventListener(FcmMessageService fcmMessageService, StoreEmployeeRepository storeEmployeeRepository) {
        this.fcmMessageService = fcmMessageService;
        this.storeEmployeeRepository = storeEmployeeRepository;
    }

    @TransactionalEventListener
    public void onOrderChange(Order order) {
        try {
            if (order.isNotified()) {
                return;
            }

            fcmMessageService.initialize();

            // 매장 ID 기반 토픽 설정
            String topic = "store_" + order.getStore().getStoreId();
            System.out.println("전송되는 Topic:" + topic);

            int totalQuantity = order.getOrderDetails()
                    .stream()
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();

            // 근무 중인 직원이 있는 경우에만 토픽 알림 전송
            if (hasActiveEmployees(order.getStore())) {
                fcmMessageService.sendToTopic(
                        topic,
                        "새로운 주문 알림",
                        "총 " + totalQuantity + "잔 새로운 주문이 들어왔습니다."
                );
            } else {
                System.out.println("근무 중인 직원이 없습니다.");
                markOrderAsNotified(order);
            }
        } catch(Exception e) {
            System.out.println("주문 알림 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 매장에 근무 중인 직원이 있는지 확인
    private boolean hasActiveEmployees(Store store) {
        List<StoreEmployee> activeEmployees = storeEmployeeRepository.findActiveEmployees(store, true);
        return !activeEmployees.isEmpty();
    }

    private void markOrderAsNotified(Order order) {
        order.setNotified(true);
        // 서비스 계층에서 저장 처리하는 것이 더 안전합니다.
    }
}
