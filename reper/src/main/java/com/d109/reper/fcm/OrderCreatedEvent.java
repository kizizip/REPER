package com.d109.reper.fcm;

import com.d109.reper.domain.Order;
import org.springframework.context.ApplicationEvent;

public class OrderCreatedEvent extends ApplicationEvent {

    private final Order order;

    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
        System.out.println("OrderCreatedEvent 실행 됨");
    }

    public  Order getOrder() {
        return  order;
    }
}
