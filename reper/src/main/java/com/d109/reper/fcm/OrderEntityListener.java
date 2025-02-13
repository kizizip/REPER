package com.d109.reper.fcm;

import com.d109.reper.domain.Order;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostPersist
    public void onOrderChange(Order order) {
        if (!order.isNotified()) {
            eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
        }
    }
}
