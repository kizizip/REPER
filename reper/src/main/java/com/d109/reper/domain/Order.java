package com.d109.reper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "takeout")
    private boolean takeout;

    @Column(name = "is_notified")
    private boolean isNotified = false;

    // 양방향 관계 설정
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // 연관관계 메서드 (OrderDetail과)
    public void addOrderDetail(OrderDetail orderDetail) {
        if (orderDetail == null) {
            throw new IllegalArgumentException("OrderDetail cannot be null");
        }
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

}
