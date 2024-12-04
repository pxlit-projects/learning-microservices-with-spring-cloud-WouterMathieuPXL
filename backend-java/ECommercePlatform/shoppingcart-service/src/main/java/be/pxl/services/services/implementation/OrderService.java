package be.pxl.services.services.implementation;

import be.pxl.services.domain.CustomerOrder;
import be.pxl.services.domain.OrderItem;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.dto.CustomerOrderResponse;
import be.pxl.services.repository.CustomerOrderRepository;
import be.pxl.services.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static be.pxl.services.services.implementation.ShoppingCartMapper.mapCustomerOrderToCustomerOrderResponse;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartHelper shoppingCartHelper;
    private final CustomerOrderRepository customerOrderRepository;

    @Transactional
    @Override
    public CustomerOrderResponse checkout(Long shoppingCartId) {
        log.info("Checking out shopping cart with ID: {}", shoppingCartId);

        ShoppingCart shoppingCart = shoppingCartHelper.findShoppingCartById(shoppingCartId);
        shoppingCartHelper.fetchProductForShoppingCartItems(shoppingCart);
        shoppingCartHelper.removeNonExistingProductsFromShoppingCart(shoppingCart);

        CustomerOrder customerOrder = CustomerOrder.builder()
                .shoppingCartId(shoppingCartId)
                .dateTime(LocalDateTime.now())
                .build();
        addOrderItemsToOrder(shoppingCart, customerOrder);
        customerOrderRepository.save(customerOrder);
        log.info("Checked out shopping cart with ID {}", shoppingCart.getId());

        shoppingCartService.clearShoppingCart(shoppingCart);

        return mapCustomerOrderToCustomerOrderResponse(customerOrder);
    }

    private void addOrderItemsToOrder(ShoppingCart shoppingCart, CustomerOrder customerOrder) {
        shoppingCart.getShoppingCartItems().forEach(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();

            customerOrder.addOrderItem(orderItem);
        });
    }
}
