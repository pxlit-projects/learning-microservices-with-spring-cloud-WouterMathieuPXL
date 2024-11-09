package be.pxl.services.services.implementation;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.ShoppingCartItem;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.domain.dto.ShoppingCartItemResponse;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.ShoppingCartRepository;
import be.pxl.services.services.IShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService implements IShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public ShoppingCartResponse getShoppingCart(Long cartId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping cart not found"));
        return mapToShoppingCartResponse(shoppingCart);
    }

    @Override
    @Transactional
    public void addProductToCart(Long cartId, Long productId, int quantity) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping cart not found"));

        // TODO: Logic to add product to cart

        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long cartId, Long productId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping cart not found"));

        // TODO: Logic to remove product from cart

        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public CustomerOrder checkout(Long cartId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping cart not found"));

        // TODO: Logic to checkout

        return new CustomerOrder();
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
        ProductResponse productResponse = ProductResponse.builder()
                .id(item.getProduct().getId())
                .name(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .build();

        return ShoppingCartItemResponse.builder()
                .id(item.getId())
                .product(productResponse)
                .quantity(item.getQuantity())
                .build();
    }
}
