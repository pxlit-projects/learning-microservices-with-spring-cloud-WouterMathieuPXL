package be.pxl.services.controller;

import be.pxl.services.domain.dto.CustomerOrderResponse;
import be.pxl.services.domain.dto.ShoppingCartResponse;
import be.pxl.services.exceptions.ForbiddenException;
import be.pxl.services.services.IOrderService;
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
    private final IOrderService orderService;

    @GetMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponse getShoppingCartById(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long shoppingCartId) {
        if (role == null || !role.equals("USER")) {
            log.warn("Unauthorized attempt to get access to the shopping card with ID {}", shoppingCartId);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to fetch shopping cart with ID {}", shoppingCartId);
        return shoppingCartService.getShoppingCartById(shoppingCartId);
    }

    @PutMapping("/{shoppingCartId}/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    // url: /api/shoppingCart/{shoppingCartId}/products/{productId}?quantity={quantity}
    public ShoppingCartResponse editProductInShoppingCart(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long shoppingCartId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        if (role == null || !role.equals("USER")) {
            log.warn("Unauthorized attempt to edit the shopping card with ID {}", shoppingCartId);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to edit shopping cart with ID {}: {}x product with ID {}",
                shoppingCartId, quantity, productId);
        return shoppingCartService.editProductInShoppingCart(shoppingCartId, productId, quantity);
    }

    @PostMapping("/{shoppingCartId}/checkout")
    @ResponseStatus(HttpStatus.OK)
    public CustomerOrderResponse checkoutShoppingCart(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long shoppingCartId) {
        if (role == null || !role.equals("USER")) {
            log.warn("Unauthorized attempt to checkout to the shopping card with ID {}", shoppingCartId);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to checkout shopping cart with ID {}", shoppingCartId);
        return orderService.checkout(shoppingCartId);
    }
}
