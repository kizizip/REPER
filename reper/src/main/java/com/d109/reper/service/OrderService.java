package com.d109.reper.service;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.OrderDetail;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.Store;
import com.d109.reper.fcm.OrderCreatedEvent;
import com.d109.reper.repository.OrderDetailRepository;
import com.d109.reper.repository.OrderRepository;
import com.d109.reper.repository.RecipeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.response.OrderResponseDto;
import jakarta.transaction.Transactional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StoreRepository storeRepository;
    private final RecipeRepository recipeRepository;
    private static final List<String> CUSTOMER_REQUESTS = List.of("시럽 추가", "샷 빼고", "연하게", "진하게", "얼음 적게");
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, StoreRepository storeRepository, RecipeRepository recipeRepository, ApplicationEventPublisher eventPublisher) {

        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.storeRepository = storeRepository;
        this.recipeRepository = recipeRepository;
        this.eventPublisher = eventPublisher;
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
        Store store = storeRepository.findById(75L)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Order order = new Order();
        order.setStore(store);
        order.setOrderDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        order.setCompleted(false);

        // takeout 여부를 랜덤으로 설정
        boolean isTakeout = new Random().nextBoolean();
        order.setTakeout(isTakeout);

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

        // 주문 생성 후 이벤트 발행
        eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
        System.out.println("이벤트 발생");

        return order;
    }


    public boolean updateOrder(Long orderId) {
        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("OrderNotFound"));

            if (order != null) {
                order.setCompleted(true);
            }

            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 주문이 1분에 하나씩 생기게 스케쥴링
//    @Scheduled(fixedDelay = 15000) // 테스트용 15초
//    @Scheduled(fixedDelay = 60000)
    @Scheduled(fixedDelay = 600000000) // 개발할때 공지 계속 오면 정신없으니 큰 값 설정 7일이래요
    public void createOrder1Min() {
        createRandomOrder();
        System.out.println("새로운 주문 생성" + LocalDateTime.now());
    }

    // 발표용 만들어진 주문 API
    @Transactional
    public OrderResponseDto createOrder(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Order order = new Order();
        order.setStore(store);
        order.setOrderDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        order.setCompleted(false);
        order.setTakeout(new Random().nextBoolean());

        order = orderRepository.saveAndFlush(order); // Flush 사용하여 즉시 반영

        addOrderDetail(order, 1839L, "얼음 적게");
        addOrderDetail(order, 1841L, "샷 추가");
        addOrderDetail(order, 1863L, "뜨겁게");
        addOrderDetail(order, 1873L, null);
        addOrderDetail(order, 1881L, "디카페인");

        // 주문 생성 후 이벤트 발행 (FCM 전송 포함)
        eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
        System.out.println("이벤트 발생");

        return new OrderResponseDto(order); // OrderResponseDto 변환 후 반환
    }

    private void addOrderDetail(Order order, Long recipeId, String customerRequest) {
        Recipe recipe = recipeRepository.findOne(recipeId);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setRecipe(recipe);
        orderDetail.setQuantity(1);
        orderDetail.setCustomerRequest(customerRequest);

        order.addOrderDetail(orderDetail);
        orderDetailRepository.save(orderDetail);
    }


}
