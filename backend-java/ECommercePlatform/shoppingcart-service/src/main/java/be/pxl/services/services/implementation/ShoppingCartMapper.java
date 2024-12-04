package be.pxl.services.services.implementation;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.OrderItem;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.ShoppingCartItem;
import be.pxl.services.domain.dto.CustomerOrderResponse;
import be.pxl.services.domain.dto.OrderItemResponse;
import be.pxl.services.domain.dto.ShoppingCartItemResponse;
import be.pxl.services.domain.dto.ShoppingCartResponse;

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
                .shoppingCartId(customerOrder.getShoppingCartId())
                .dateTime(customerOrder.getDateTime())
                .orderItems(
                        customerOrder.getOrderItems().stream()
                                .map(ShoppingCartMapper::mapOrderItemToOrderItemResponse).toList())
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
