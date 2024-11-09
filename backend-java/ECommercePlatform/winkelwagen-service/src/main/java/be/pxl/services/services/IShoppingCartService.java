package be.pxl.services.services;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.dto.ShoppingCartResponse;

public interface IShoppingCartService {

    ShoppingCartResponse getShoppingCart(Long cartId);
    void addProductToCart(Long cartId, Long productId, int quantity);
    void removeProductFromCart(Long cartId, Long productId);

    CustomerOrder checkout(Long cartId);
}
