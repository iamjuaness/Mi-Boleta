package com.microservice.cart.service.interfaces;

import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.persistence.model.vo.EventVO;
import com.microservice.cart.presentation.dto.PurchaseOrderDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    State addToCart(String idUser, EventVO eventVO);
    State deleteToCart(String idUser, String idEventVO);
    State upgradeQuantity(String idUser, String idEventVO, int quantity);
    List<EventVO> getCart(String idUser);
    State clearCart(String idUser);
    State applyDiscount(String idUser, String discountCode);
    BigDecimal calculateTotal(String idUser);
    PurchaseOrderDTO checkout(String idUser);
}
