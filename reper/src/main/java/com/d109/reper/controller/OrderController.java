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

    @GetMapping
    public List<Order> getOrdersByStoreId(@PathVariable Long storeId) {
//        return orderService.findOrdersByStoreId(storeId);
        System.out.println("Store ID from request: " + storeId);
        return orderService.findOrdersByStoreId(storeId);
    }
}
