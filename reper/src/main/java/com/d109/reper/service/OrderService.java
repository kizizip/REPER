package com.d109.reper.service;

import com.d109.reper.domain.Order;
import com.d109.reper.repository.OrderRepository;
import com.d109.reper.response.OrderResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findOrdersByStoreId(Long storeId) {
        List<Order> orders = orderRepository.findByStore_StoreId(storeId);
        System.out.println("Orders found: " + orders);
        return orders;
    }
}
