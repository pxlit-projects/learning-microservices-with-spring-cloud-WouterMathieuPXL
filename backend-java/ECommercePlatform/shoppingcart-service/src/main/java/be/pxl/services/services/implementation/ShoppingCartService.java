package be.pxl.services.services.implementation;

import be.pxl.services.client.ProductCatalogClient;
import be.pxl.services.domain.*;
import be.pxl.services.domain.dto.CustomerOrderResponse;
import be.pxl.services.domain.dto.OrderItemResponse;
import be.pxl.services.domain.dto.ShoppingCartItemResponse;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.CustomerOrderRepository;
import be.pxl.services.repository.ShoppingCartRepository;
import be.pxl.services.services.IShoppingCartService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService implements IShoppingCartService {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartService.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerOrderRepository customerOrderRepository;
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
    public CustomerOrderResponse checkout(Long shoppingCartId) {
        log.info("Checking out shopping cart with ID: {}", shoppingCartId);
        ShoppingCart shoppingCart = findShoppingCartById(shoppingCartId);

        // Set product for every item
        shoppingCart.getShoppingCartItems()
                .forEach(cartItem -> cartItem.setProduct(getProductFromProductId(cartItem.getProductId())));

        // Transactional
        boolean itemsRemoved = removeItemsWithoutProductAndSave(shoppingCart);
        if (itemsRemoved) {
            throw new ResourceNotFoundException(
                    "One or more products were not found in the catalog and have been removed. Order cannot proceed.");
        }

        return createCustomerOrder(shoppingCart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean removeItemsWithoutProductAndSave(ShoppingCart shoppingCart) {
        boolean itemsRemoved = shoppingCart.getShoppingCartItems().removeIf(cartItem -> {
            boolean shouldRemove = cartItem.getProduct() == null;
            if (shouldRemove) {
                log.info("Product with ID: {} removed from shopping cart", cartItem.getProductId());
            }
            return shouldRemove;
        });

        if (itemsRemoved) {
            shoppingCartRepository.save(shoppingCart);
        }

        return itemsRemoved;
    }

    @Transactional
    public CustomerOrderResponse createCustomerOrder(ShoppingCart shoppingCart) {
        CustomerOrder customerOrder = CustomerOrder.builder()
                .dateTime(LocalDateTime.now())
                .build();
        customerOrderRepository.save(customerOrder);

        // Add items to the order
        shoppingCart.getShoppingCartItems().forEach(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();

            customerOrder.addOrderItem(orderItem);
        });

        customerOrderRepository.save(customerOrder);
        log.info("Checked out shopping cart with ID: {}", shoppingCart.getId());

        // Clear the shopping cart
        shoppingCart.getShoppingCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        log.info("Cleared shopping cart with ID: {}", shoppingCart.getId());

        return mapCustomerOrderToCustomerOrderResponse(customerOrder);
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

    public CustomerOrderResponse mapCustomerOrderToCustomerOrderResponse(CustomerOrder customerOrder) {
        return CustomerOrderResponse.builder()
                .id(customerOrder.getId())
                .dateTime(customerOrder.getDateTime())
                .orderItems(
                        customerOrder.getOrderItems().stream()
                                .map(this::mapOrderItemToOrderItemResponse).toList())
                .build();
    }

    public OrderItemResponse mapOrderItemToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
