package com.d109.reper.response;

import com.d109.reper.domain.Order;
import com.d109.reper.domain.OrderDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class OrderResponseDto {

        private Long orderId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime orderDate;
        private boolean isCompleted;
        private boolean takeout;
        private List<OrderDetailDto> orderDetails;

        public OrderResponseDto(Order order) {
            this.orderId = order.getOrderId();
            this.orderDate = order.getOrderDate();
            this.isCompleted = order.isCompleted();
            this.takeout = order.isTakeout();
            this.orderDetails = order.getOrderDetails().stream()
                    .map(OrderDetailDto::new)
                    .collect(Collectors.toList());
        }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class OrderDetailDto {
            private Long orderDetailId;
            private Long recipeId;
            private int quantity;
            private String customerRequest;

        public OrderDetailDto(OrderDetail orderDetail) {
            this.orderDetailId = orderDetail.getOrderDetailId();
            this.recipeId = orderDetail.getRecipe().getRecipeId();
            this.quantity = orderDetail.getQuantity();
            this.customerRequest = orderDetail.getCustomerRequest();
        }
    }


}
