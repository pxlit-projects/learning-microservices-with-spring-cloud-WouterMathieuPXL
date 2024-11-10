package be.pxl.services.controller;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.services.IShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/winkelwagen")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final IShoppingCartService shoppingCartService;

    @GetMapping("/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponse getShoppingCart(@PathVariable Long cartId) {
        return shoppingCartService.getShoppingCart(cartId);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponse addProductToCart(@RequestParam Long cartId, @RequestParam Long productId,
                                         @RequestParam int quantity) {
        shoppingCartService.addProductToCart(cartId, productId, quantity);
        return shoppingCartService.getShoppingCart(cartId);
    }

    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponse removeProductFromCart(@RequestParam Long cartId, @RequestParam Long productId) {
        shoppingCartService.removeProductFromCart(cartId, productId);
        return shoppingCartService.getShoppingCart(cartId);
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.OK)
    public CustomerOrder checkout(@RequestParam Long cartId) {
        return shoppingCartService.checkout(cartId);
    }
}