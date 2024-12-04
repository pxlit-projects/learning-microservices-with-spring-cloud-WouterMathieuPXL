package be.pxl.services.domain.dto;

import lombok.*;

import java.util.Set;

@Getter
@Builder
public class ShoppingCartResponse {

    private Long id;
    private Set<ShoppingCartItemResponse> shoppingCartItems;
}
