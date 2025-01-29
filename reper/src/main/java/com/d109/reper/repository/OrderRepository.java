package com.d109.reper.repository;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStore_StoreId(Long storeId);
    List<Order> findByStore(Store store); //위 메서드가 작동하지 않아서 만듦
}
