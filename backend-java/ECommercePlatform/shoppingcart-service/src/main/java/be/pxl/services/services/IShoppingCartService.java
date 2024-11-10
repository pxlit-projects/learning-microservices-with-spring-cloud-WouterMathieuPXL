package be.pxl.services.services;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.dto.ShoppingCartResponse;

public interface IShoppingCartService {

    ShoppingCartResponse getShoppingCartById(Long shoppingCartId);
    void addProductToShoppingCart(Long shoppingCartId, Long productId, int quantity);
    void updateProductQuantityInShoppingCart(Long shoppingCartId, Long productId, int quantity);
    void removeProductFromShoppingCart(Long shoppingCartId, Long productId);

    CustomerOrder checkout(Long shoppingCartId);
}
