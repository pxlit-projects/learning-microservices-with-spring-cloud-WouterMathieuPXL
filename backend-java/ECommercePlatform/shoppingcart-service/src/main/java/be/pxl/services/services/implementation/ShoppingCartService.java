package be.pxl.services.services.implementation;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.ShoppingCartItem;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.repository.ShoppingCartRepository;
import be.pxl.services.services.IShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static be.pxl.services.services.implementation.ShoppingCartMapper.mapToShoppingCartResponse;

@Service
@RequiredArgsConstructor
public class ShoppingCartService implements IShoppingCartService {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartService.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartHelper shoppingCartHelper;

    @Override
    public ShoppingCartResponse getShoppingCartById(Long shoppingCartId) {
        log.info("Fetching shopping cart with ID: {}", shoppingCartId);
        ShoppingCart shoppingCart = shoppingCartHelper.findShoppingCartById(shoppingCartId);
        return mapToShoppingCartResponse(shoppingCart);
    }

    @Override
    public ShoppingCartResponse editProductInShoppingCart(Long shoppingCartId, Long productId, int quantity) {
        log.info("Editing shopping cart with ID {}", shoppingCartId);

        ShoppingCart shoppingCart = shoppingCartHelper.findShoppingCartById(shoppingCartId); // Throws not found if
        // not existing
        Product product = shoppingCartHelper.getProductFromProductId(productId); // Throws not found if not existing
        shoppingCartHelper.fetchProductForShoppingCartItems(shoppingCart);
        shoppingCartHelper.removeNonExistingProductsFromShoppingCart(shoppingCart);
        ShoppingCartItem shoppingCartItem = shoppingCartHelper.getShoppingCartItemFromProductId(shoppingCart,
                productId);

        if (quantity <= 0) {
            removeProductFromShoppingCart(shoppingCart, shoppingCartItem);
        } else if (shoppingCartItem != null) {
            updateProductQuantityInShoppingCart(shoppingCartItem, productId, quantity);
        } else {
            addProductToShoppingCart(shoppingCart, product, quantity);
        }

        shoppingCartRepository.save(shoppingCart);
        shoppingCartHelper.fetchProductForShoppingCartItems(shoppingCart);

        return mapToShoppingCartResponse(shoppingCart);
    }

    private void addProductToShoppingCart(ShoppingCart shoppingCart, Product product, int quantity) {
        shoppingCart.getShoppingCartItems().add(ShoppingCartItem.builder()
                .shoppingCart(shoppingCart)
                .productId(product.getId())
                .quantity(quantity)
                .build());
        log.info("Product with ID {} added to shopping cart with ID {}", product.getId(), shoppingCart.getId());
    }

    private void updateProductQuantityInShoppingCart(ShoppingCartItem shoppingCartItem, Long productId, int quantity) {
        shoppingCartItem.setQuantity(quantity);
        log.info("Product with ID {} updated in shopping cart with ID {}", productId,
                shoppingCartItem.getShoppingCart().getId());
    }

    private void removeProductFromShoppingCart(ShoppingCart shoppingCart, ShoppingCartItem shoppingCartItem) {
        if (shoppingCartItem != null) {
            shoppingCart.getShoppingCartItems().remove(shoppingCartItem);
            log.info("Product with ID {} removed from shopping cart with ID {}",
                    shoppingCartItem.getProduct().getId(), shoppingCart.getId());
        }
    }

    public void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getShoppingCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        log.info("Cleared the shopping cart with ID {}", shoppingCart.getId());
    }
}
