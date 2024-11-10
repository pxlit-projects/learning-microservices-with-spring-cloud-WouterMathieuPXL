package be.pxl.services.services.implementation;

import be.pxl.services.client.ProductCatalogClient;
import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.ShoppingCartItem;
import be.pxl.services.domain.dto.ShoppingCartItemResponse;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.ShoppingCartRepository;
import be.pxl.services.services.IShoppingCartService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService implements IShoppingCartService {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartService.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductCatalogClient productCatalogClient;

    @Override
    public ShoppingCartResponse getShoppingCartById(Long shoppingCartId) {
        log.info("Fetching shopping cart with ID: {}", shoppingCartId);
        ShoppingCart shoppingCart = findShoppingCartById(shoppingCartId);
        return mapToShoppingCartResponse(shoppingCart);
    }

    @Override
    @Transactional
    public void addProductToShoppingCart(Long shoppingCartId, Long productId, int quantity) {
        log.info("Adding product with ID: {} to shopping cart with ID: {}", productId, shoppingCartId);
        ShoppingCart shoppingCart = findShoppingCartById(shoppingCartId);

        // Check if product exists
        Product product = getProductFromProductId(productId);
        if (product == null) {
            log.error("Product with ID: {} not found in catalog", productId);
            throw new ResourceNotFoundException("Product not found in catalog");
        }

        // Set product for all items in the shopping cart
        shoppingCart.getShoppingCartItems().forEach(cartItem ->
                cartItem.setProduct(productCatalogClient.getProductById(cartItem.getProductId()))
        );

        // Checks if the product is already in the shopping cart.
        ShoppingCartItem shoppingCartItem = shoppingCart.getShoppingCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        // If the product is not in the cart, it creates a new ShoppingCartItem and adds it to the cart.
        // If the product is already in the cart, it updates the quantity.
        if (shoppingCartItem == null) {
            shoppingCartItem = ShoppingCartItem.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .shoppingCart(shoppingCart)
                    .build();
            shoppingCart.getShoppingCartItems().add(shoppingCartItem);
        } else {
            shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + quantity);
        }

        shoppingCartRepository.save(shoppingCart);
        log.info("Product with ID: {} added to shopping cart with ID: {}", productId, shoppingCartId);
    }

    @Override
    @Transactional
    public void updateProductQuantityInShoppingCart(Long shoppingCartId, Long productId, int quantity) {
        log.info("Updating quantity of product with ID: {} in shopping cart with ID: {}", productId, shoppingCartId);
        ShoppingCart shoppingCart = findShoppingCartById(shoppingCartId);

        ShoppingCartItem shoppingCartItem = shoppingCart.getShoppingCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found in shopping cart", productId);
                    return new ResourceNotFoundException("Product not found in shopping cart");
                });

        if (quantity > 0) {
            shoppingCartItem.setQuantity(quantity);
            log.info("Quantity of product with ID: {} updated in shopping cart with ID: {}", productId, shoppingCartId);
        } else {
            shoppingCart.getShoppingCartItems().remove(shoppingCartItem);
            log.info("Product with ID: {} removed from shopping cart with ID: {}", productId, shoppingCartId);
        }

        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void removeProductFromShoppingCart(Long shoppingCartId, Long productId) {
        log.info("Removing product with ID: {} from shopping cart with ID: {}", productId, shoppingCartId);
        ShoppingCart shoppingCart = findShoppingCartById(shoppingCartId);

        ShoppingCartItem shoppingCartItem = shoppingCart.getShoppingCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found in shopping cart", productId);
                    return new ResourceNotFoundException("Product not found in shopping cart");
                });

        shoppingCart.getShoppingCartItems().remove(shoppingCartItem);

        shoppingCartRepository.save(shoppingCart);
        log.info("Product with ID: {} removed from shopping cart with ID: {}", productId, shoppingCartId);
    }

    @Override
    @Transactional
    public CustomerOrder checkout(Long shoppingCartId) {
        log.info("Checking out shopping cart with ID: {}", shoppingCartId);
        ShoppingCart shoppingCart = findShoppingCartById(shoppingCartId);

        // TODO: Logic to checkout

        return new CustomerOrder();
    }

    private ShoppingCart findShoppingCartById(Long shoppingCartId) {
        return shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> {
                    log.error("Shopping cart with ID: {} not found", shoppingCartId);
                    return new ResourceNotFoundException("Shopping cart not found");
                });
    }

    private Product getProductFromProductId(Long productId) {
        try {
            return productCatalogClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            System.out.println("Product with ID " + productId + " not found in ProductCatalogService.");
            return null;
        }
    }

    private ShoppingCartResponse mapToShoppingCartResponse(ShoppingCart shoppingCart) {
        Set<ShoppingCartItemResponse> items = shoppingCart.getShoppingCartItems().stream()
                .map(this::mapToShoppingCartItemResponse)
                .collect(Collectors.toSet());

        return ShoppingCartResponse.builder()
                .id(shoppingCart.getId())
                .shoppingCartItems(items)
                .build();
    }

    private ShoppingCartItemResponse mapToShoppingCartItemResponse(ShoppingCartItem item) {
        return ShoppingCartItemResponse.builder()
                .id(item.getId())
                .product(getProductFromProductId(item.getProductId()))
                .quantity(item.getQuantity())
                .build();
    }
}
