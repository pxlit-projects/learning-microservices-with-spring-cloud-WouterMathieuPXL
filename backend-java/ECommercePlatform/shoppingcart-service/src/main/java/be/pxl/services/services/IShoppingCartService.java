package be.pxl.services.services;

import be.pxl.services.domain.dto.ShoppingCartResponse;

public interface IShoppingCartService {

    ShoppingCartResponse getShoppingCartById(Long shoppingCartId);
    ShoppingCartResponse editProductInShoppingCart(Long shoppingCartId, Long productId, int quantity);
}
