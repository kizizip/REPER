package com.d109.reper.repository;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStore_StoreId(Long storeId);
    Optional<Order> findByStore_StoreIdAndOrderId(Long storeId, Long orderId);
    Optional<Order> findByOrderId(Long orderId);

}
