package com.d109.reper.controller;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.OrderDetail;
import com.d109.reper.response.OrderResponseDto;
import com.d109.reper.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stores/{storeId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 특정 매장의 전체 주문 조회하기
    @GetMapping
    @Operation(summary = "특정 매장의 전체 주문 내역을 조회합니다.", description = "한 매장의 단순 전체 주문내역")
    public List<OrderResponseDto> getOrdersByStoreId(@PathVariable Long storeId) {
        System.out.println("Store ID from request: " + storeId);
        return orderService.findOrdersByStoreId(storeId);
    }


    // 특정 매장의 특정 주문 조회하기
    @GetMapping("/{orderId}")
    @Operation(summary = "특정 매장의 특정 주문 한 건을 조회합니다.", description = "한 주문건에 여러 음료가 포함될 수 있습니다.")
    public OrderResponseDto getOrderById(@PathVariable Long storeId, @PathVariable Long orderId) {
        return orderService.findOrderByStoreIdAndOrderId(storeId, orderId);
    }


    // 랜덤으로 더미데이터 생성 (주문 한 건씩)
    // storeId는 68L로 통일합니다. 수정이 필요할 시 추후에 요청해 주세요.
    @PostMapping
    @Operation(summary = "클릭시 주문 한 건씩 추가합니다.", description = "주문 한 건당 1~3개의 음료(recipe)가 포함됩니다. storeId는 68로 통일. 수정이 필요하다면 추후 요청해 주세요.")
    public ResponseEntity<OrderResponseDto> createOrder() {
        Order order = orderService.createRandomOrder();

        // OrderResponseDto로 변환
        OrderResponseDto orderResponseDto = new OrderResponseDto(order);

        return ResponseEntity.ok(orderResponseDto);
    }


    // 특정 주문 단건 완료 처리
    @PatchMapping("/{orderId}")
    @Operation(summary = "주문 단건의 is_completed값을 true로 전환합니다. 성공시 true를 반환합니다.")
    public ResponseEntity<?> completeOrder(@PathVariable Long orderId) {
        try {
            boolean result = orderService.updateOrder(orderId);

            if (result) {
                return ResponseEntity.ok(true);
            } else {
                throw new NoSuchElementException("해당하는 주문이 없음.");
            }
        } catch (Exception e) {
            throw new RuntimeException("서버 오류 발생");
        }
    }

    // 시연용 정해진 주문 API
    @PostMapping("/create")
    @Operation(summary = "발표용 주문 API")
    public ResponseEntity<OrderResponseDto> createJpaOrder(@PathVariable Long storeId) {
        OrderResponseDto orderResponseDto = orderService.createOrder(storeId);
        return ResponseEntity.ok(orderResponseDto);
    }
}
