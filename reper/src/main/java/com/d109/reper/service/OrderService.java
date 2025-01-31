package com.d109.reper.service;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.OrderDetail;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.Store;
import com.d109.reper.repository.OrderDetailRepository;
import com.d109.reper.repository.OrderRepository;
import com.d109.reper.repository.RecipeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.response.OrderResponseDto;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StoreRepository storeRepository;
    private final RecipeRepository recipeRepository;
    private static final List<String> CUSTOMER_REQUESTS = List.of("시럽 추가", "샷 빼고", "연하게", "진하게", "얼음 적게");

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, StoreRepository storeRepository, RecipeRepository recipeRepository) {

        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.storeRepository = storeRepository;
        this.recipeRepository = recipeRepository;
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


    @Transactional
    public Order createRandomOrder() {
        Store store = storeRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Order order = new Order();
        order.setStore(store);
        order.setOrderDate(LocalDateTime.now());
        order.setCompleted(false);

        List<Recipe> recipes = recipeRepository.findAllRecipes();
        if (recipes.isEmpty()) {
            throw new IllegalStateException("No recipes available");
        }

        //랜덤하게 1~3개의 OrderDetail을 추가
        Collections.shuffle(recipes);
        int orderDetailCount = new Random().nextInt(3) +1;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (int i = 0; i <orderDetailCount ; i++) {
            Recipe recipe = recipes.get(i);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setRecipe(recipe);
            orderDetail.setQuantity(new Random().nextInt(3)+1);
            orderDetail.setCustomerRequest(CUSTOMER_REQUESTS.get(new Random().nextInt(CUSTOMER_REQUESTS.size())));

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        return order;
    }

}
