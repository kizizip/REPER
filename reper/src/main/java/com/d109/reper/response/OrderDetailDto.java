//package com.d109.reper.response;
//
//import com.d109.reper.domain.OrderDetail;
//import lombok.Data;
//
//@Data
//public class OrderDetailDto {
//    private Long recipeId;
//    private String recipeName;
//    private int quantity;
//    private String customerRequest;
//
//    public static OrderDetailDto fromEntity(OrderDetail orderDetail) {
//        OrderDetailDto dto = new OrderDetailDto();
//        dto.setRecipeId(orderDetail.getRecipe().getRecipeId());
//        dto.setRecipeName(orderDetail.getRecipe().getRecipeName());
//        dto.setQuantity(orderDetail.getQuantity());
//        dto.setCustomerRequest(orderDetail.getCustomerRequest());
//        return dto;
//    }
//}
