package be.pxl.services.services;

import be.pxl.services.domain.dto.CustomerOrderResponse;

public interface IOrderService {

    CustomerOrderResponse checkout(Long shoppingCartId);
}
