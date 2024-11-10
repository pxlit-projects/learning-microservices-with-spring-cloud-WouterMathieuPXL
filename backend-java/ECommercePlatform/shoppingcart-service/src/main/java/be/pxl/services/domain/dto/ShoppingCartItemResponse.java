package be.pxl.services.domain.dto;

import be.pxl.services.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemResponse {

    private Long id;
    private Product product;
    private int quantity;
}
