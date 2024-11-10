package be.pxl.services.mapper;

import be.pxl.services.domain.*;
import be.pxl.services.domain.dto.*;

import java.util.Set;
import java.util.stream.Collectors;

public class ShoppingCartMapper {

    public static ShoppingCartResponse mapToShoppingCartResponse(ShoppingCart shoppingCart) {
        Set<ShoppingCartItemResponse> items = shoppingCart.getShoppingCartItems().stream()
                .map(ShoppingCartMapper::mapToShoppingCartItemResponse)
                .collect(Collectors.toSet());

        return ShoppingCartResponse.builder()
                .id(shoppingCart.getId())
                .shoppingCartItems(items)
                .build();
    }

    public static ShoppingCartItemResponse mapToShoppingCartItemResponse(ShoppingCartItem item) {
        return ShoppingCartItemResponse.builder()
                .id(item.getId())
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .build();
    }

    public static CustomerOrderResponse mapCustomerOrderToCustomerOrderResponse(CustomerOrder customerOrder) {
        return CustomerOrderResponse.builder()
                .id(customerOrder.getId())
                .dateTime(customerOrder.getDateTime())
                .orderItems(customerOrder.getOrderItems().stream()
                        .map(ShoppingCartMapper::mapOrderItemToOrderItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static OrderItemResponse mapOrderItemToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }


}
