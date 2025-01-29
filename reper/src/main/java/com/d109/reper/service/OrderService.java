package com.d109.reper.service;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.Store;
import com.d109.reper.repository.OrderRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.response.OrderResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    public OrderService(OrderRepository orderRepository, StoreRepository storeRepository) {

        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
    }

    public List<OrderResponseDto> findOrdersByStoreId(Long storeId) {
        List<Order> orders = orderRepository.findByStore_StoreId(storeId);
        System.out.println("Orders found: " + orders);
        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    public OrderResponseDto findOrderByStoreIdAndOrderId(Long storeId, Long orderId) {
        Order order = orderRepository.findByStore_StoreIdAndOrderId(storeId, orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return new OrderResponseDto(order);
    }

}
