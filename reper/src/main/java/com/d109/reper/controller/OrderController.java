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
    // storeId는 1L로 통일합니다. 수정이 필요할 시 추후에 요청해 주세요.
    @PostMapping
    @Operation(summary = "클릭시 주문 한 건씩 추가합니다.", description = "주문 한 건당 1~3개의 음료(recipe)가 포함됩니다. storeId는 1로 통일. 수정이 필요하다면 추후 요청해 주세요.")
    public ResponseEntity<OrderResponseDto> createOrder() {
        Order order = orderService.createRandomOrder();

        // OrderResponseDto로 변환
        OrderResponseDto orderResponseDto = new OrderResponseDto(order);

        return ResponseEntity.ok(orderResponseDto);
    }
}
