package be.pxl.services.domain.dto;

import be.pxl.services.domain.Product;
import lombok.*;

@Getter
@Builder
public class ShoppingCartItemResponse {

    private Long id;
    private Product product;
    private int quantity;
}
