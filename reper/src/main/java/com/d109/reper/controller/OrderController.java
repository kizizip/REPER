package com.d109.reper.controller;

import com.d109.reper.domain.Order;
import com.d109.reper.response.OrderResponseDto;
import com.d109.reper.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<OrderResponseDto> getOrdersByStoreId(@PathVariable Long storeId) {
//        return orderService.findOrdersByStoreId(storeId);
        System.out.println("Store ID from request: " + storeId);
        return orderService.findOrdersByStoreId(storeId);
    }


    // 특정 매장의 특정 주문 조회하기
    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long storeId, @PathVariable Long orderId) {
        return orderService.findOrderByStoreIdAndOrderId(storeId, orderId);
    }
}
