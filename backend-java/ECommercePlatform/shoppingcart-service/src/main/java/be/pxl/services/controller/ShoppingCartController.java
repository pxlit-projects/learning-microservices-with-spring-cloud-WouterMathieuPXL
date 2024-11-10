package be.pxl.services.controller;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.services.IShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoppingcart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final IShoppingCartService shoppingCartService;

    @GetMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponse getShoppingCartById(@PathVariable Long shoppingCartId) {
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    // url: /api/shoppingcart/{shoppingCartId}/products?productId={productId}&quantity={quantity}
    public ShoppingCartResponse addProductToShoppingCart(
            @PathVariable Long shoppingCartId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
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
        shoppingCartService.updateProductQuantityInShoppingCart(shoppingCartId, productId, quantity);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/products")
    @ResponseStatus(HttpStatus.OK)
    // url: /api/shoppingcart/{shoppingCartId}/products?productId={productId}
    public ShoppingCartResponse removeProductFromShoppingCart(
            @PathVariable Long shoppingCartId,
            @RequestParam Long productId) {
        shoppingCartService.removeProductFromShoppingCart(shoppingCartId, productId);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/checkout")
    @ResponseStatus(HttpStatus.OK)
    public CustomerOrder checkoutShoppingCart(@PathVariable Long shoppingCartId) {
        return shoppingCartService.checkout(shoppingCartId);
    }
}
