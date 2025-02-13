package com.d109.reper.fcm;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.OrderDetail;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.repository.OrderRepository;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.service.FcmMessageService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventListener {
    private final FcmMessageService fcmMessageService;

    private final StoreEmployeeRepository storeEmployeeRepository;

    private final OrderRepository orderRepository;

    public OrderEventListener(FcmMessageService fcmMessageService, StoreEmployeeRepository storeEmployeeRepository, OrderRepository orderRepository) {
        this.fcmMessageService = fcmMessageService;
        this.storeEmployeeRepository = storeEmployeeRepository;
        this.orderRepository = orderRepository;
    }

    @EventListener
    public void onOrderChange(OrderCreatedEvent event) {
        Order order = event.getOrder();
        processOrderNotification(order);
        System.out.println("onOrderChange 실행 됨");
    }

    public void processOrderNotification(Order order) {
        try {
            if (order.isNotified()) {
                return;
            }
            System.out.println("processOrderNotification 실행 중");

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
                System.out.println("sendToTopic에 알림 전송 요청");
                order.setNotified(true);
                orderRepository.save(order);
            } else {
                System.out.println("근무 중인 직원이 없습니다.");
                order.setNotified(true);
                orderRepository.save(order);
            }
        } catch(Exception e) {
            System.out.println("주문 알림 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            order.setNotified(false);
            orderRepository.save(order);
        }
    }

    // 매장에 근무 중인 직원이 있는지 확인
    private boolean hasActiveEmployees(Store store) {
        List<StoreEmployee> activeEmployees = storeEmployeeRepository.findActiveEmployees(store, true);
        return !activeEmployees.isEmpty();
    }


}
