package com.d109.reper.response;

import com.d109.reper.domain.Order;
import lombok.Data;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponseDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private Long storeId;
    private boolean completed;
    private List<OrderDetailDto> orderDetails;

    public static OrderResponseDto fromEntity(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStoreId(order.getOrderDetails().isEmpty() ? null : order.getOrderDetails().get(0).getOrder().getStore().getStoreId());
        dto.setCompleted(order.isCompleted());
        dto.setOrderDetails(order.getOrderDetails().stream()
                .map(OrderDetailDto::fromEntity)
                .collect(Collectors.toList()));

        return dto;
    }


}
