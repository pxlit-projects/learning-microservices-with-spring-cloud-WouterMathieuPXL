package be.pxl.services.services.implementation;

import be.pxl.services.client.ProductCatalogClient;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.ShoppingCartItem;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingCartHelper {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartHelper.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductCatalogClient productCatalogClient;

    public ShoppingCart findShoppingCartById(Long shoppingCartId) {
        return shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> {
                    log.error("Shopping cart with ID: {} not found", shoppingCartId);
                    return new ResourceNotFoundException("Shopping cart not found");
                });
    }

    public Product getProductFromProductId(Long productId) {
        try {
            return productCatalogClient.getProductById(productId);
        } catch (Exception e) {
            log.error("Product with ID: {} not found in catalog", productId);
            throw new ResourceNotFoundException("Product not found in catalog");
        }
    }

    public void fetchProductForShoppingCartItems(ShoppingCart shoppingCart) {
        shoppingCart.getShoppingCartItems().forEach(cartItem ->
                cartItem.setProduct(productCatalogClient.getProductById(cartItem.getProductId()))
        );
    }

    public void removeNonExistingProductsFromShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getShoppingCartItems().removeIf(cartItem -> cartItem.getProduct() == null);
    }

    public ShoppingCartItem getShoppingCartItemFromProductId(ShoppingCart shoppingCart, Long productId) {
        return shoppingCart.getShoppingCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
