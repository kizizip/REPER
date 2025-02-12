package com.d109.reper.fcm;

import com.d109.reper.domain.Order;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

public class OrderEntityListener {

    @PostPersist
    @PostUpdate
    public void onOrderChange(Order order) {

//        OrderEventPublisher.publish(order);

    }
}
