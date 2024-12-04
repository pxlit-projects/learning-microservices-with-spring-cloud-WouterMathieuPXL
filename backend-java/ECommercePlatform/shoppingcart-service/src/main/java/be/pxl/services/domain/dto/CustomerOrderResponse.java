package be.pxl.services.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CustomerOrderResponse {

    private Long id;
    private Long shoppingCartId;
    private LocalDateTime dateTime;
    private List<OrderItemResponse> orderItems;
}
