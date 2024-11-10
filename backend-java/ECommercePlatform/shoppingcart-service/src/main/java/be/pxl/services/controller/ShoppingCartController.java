package be.pxl.services.controller;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.dto.CustomerOrderResponse;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.services.IShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoppingcart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartController.class);

    private final IShoppingCartService shoppingCartService;

    @GetMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponse getShoppingCartById(@PathVariable Long shoppingCartId) {
        log.info("Received request to fetch shopping cart with ID: {}", shoppingCartId);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    // url: /api/shoppingcart/{shoppingCartId}/products?productId={productId}&quantity={quantity}
    public ShoppingCartResponse addProductToShoppingCart(
            @PathVariable Long shoppingCartId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        log.info("Received request to add product with ID: {} to shopping cart with ID: {} and quantity: {}",
                productId, shoppingCartId, quantity);
        shoppingCartService.addProductToShoppingCart(shoppingCartId, productId, quantity);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @PatchMapping("/{shoppingCartId}/products")
    @ResponseStatus(HttpStatus.OK)
    // url: /api/shoppingcart/{shoppingCartId}/products?productId={productId}&quantity={quantity}
    public ShoppingCartResponse updateProductQuantityInShoppingCart(
            @PathVariable Long shoppingCartId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        log.info("Received request to update quantity of product with ID: {} in shopping cart with ID: {} to " +
                "quantity: {}", productId, shoppingCartId, quantity);
        shoppingCartService.updateProductQuantityInShoppingCart(shoppingCartId, productId, quantity);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/products")
    @ResponseStatus(HttpStatus.OK)
    // url: /api/shoppingcart/{shoppingCartId}/products?productId={productId}
    public ShoppingCartResponse removeProductFromShoppingCart(
            @PathVariable Long shoppingCartId,
            @RequestParam Long productId) {
        log.info("Received request to remove product with ID: {} from shopping cart with ID: {}",
                productId, shoppingCartId);
        shoppingCartService.removeProductFromShoppingCart(shoppingCartId, productId);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/checkout")
    @ResponseStatus(HttpStatus.OK)
    public CustomerOrderResponse checkoutShoppingCart(@PathVariable Long shoppingCartId) {
        log.info("Received request to checkout shopping cart with ID: {}", shoppingCartId);
        return shoppingCartService.checkout(shoppingCartId);
    }
}
