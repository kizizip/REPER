package com.d109.reper.fcm;

import com.d109.reper.domain.*;
import com.d109.reper.repository.OrderRepository;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.repository.UserTokenRepository;
import com.d109.reper.request.FcmMessageRequest;
import com.d109.reper.service.FcmMessageService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventListener {
    private final FcmMessageService fcmMessageService;

    private final StoreEmployeeRepository storeEmployeeRepository;

    private final OrderRepository orderRepository;

    private final UserTokenRepository userTokenRepository;

    public OrderEventListener(FcmMessageService fcmMessageService, StoreEmployeeRepository storeEmployeeRepository, OrderRepository orderRepository, UserTokenRepository userTokenRepository) {
        this.fcmMessageService = fcmMessageService;
        this.storeEmployeeRepository = storeEmployeeRepository;
        this.orderRepository = orderRepository;
        this.userTokenRepository = userTokenRepository;
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

            int totalQuantity = order.getOrderDetails()
                    .stream()
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();

            String title = "새로운 주문 알림";
            String body = order.getStore().getStoreName()+ "에 총" + totalQuantity + "잔 새로운 주문이 들어왔습니다.";
            String targetFragment = "OrderRecipeFragment";
            Integer requestId = Math.toIntExact(order.getOrderId());  // Long → Integer 변환

            // 사장님에게 보내기
            User owner = order.getStore().getOwner();
            if (owner == null) {
                throw new IllegalArgumentException("storeOwner not found");
            }

            UserToken ownerToken = userTokenRepository.findByUserId(owner.getUserId())
                    .orElseThrow(() -> new  IllegalArgumentException("ownerToken not found"));

            if (owner != null && ownerToken != null) {
                FcmMessageRequest ownerMessage = new FcmMessageRequest(
                        ownerToken.getToken(), title, body, targetFragment, requestId
                );
                fcmMessageService.sendFcmMessage(ownerMessage);
                System.out.println("사장님에게 FCM 메시지 전송 완료");
            }

            // 알바생들에게 보내기(isEmployed=true인 사람만)
            List<User> staffs = storeEmployeeRepository.findByUserAndIsEmployedTrue(order.getStore().getStoreId());

            //메세지 보내기

            order.setNotified(true);
            orderRepository.save(order);

        } catch(Exception e) {
            System.out.println("주문 알림 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            order.setNotified(false);
            orderRepository.save(order);
        }
    }

}
