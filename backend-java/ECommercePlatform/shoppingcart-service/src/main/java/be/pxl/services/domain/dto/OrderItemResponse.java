package be.pxl.services.domain.dto;

import lombok.*;

@Getter
@Builder
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}