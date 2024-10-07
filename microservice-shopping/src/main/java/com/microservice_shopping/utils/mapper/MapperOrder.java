package com.microservice_shopping.utils.mapper;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import org.springframework.stereotype.Service;

@Service
public class MapperOrder {

    public PurchaseOrder dtoOrderToEntity (PurchaseOrderDTO order) {
        PurchaseOrder orderEntity = new PurchaseOrder();
        orderEntity.setIdOrder(order.idOrder());

        orderEntity.setIdUser(order.idUser());
        orderEntity.setEmailUser(order.emailUser());
        orderEntity.setStateOrder(State.PENDING);
        orderEntity.setCart(order.cart());
        orderEntity.setTransactionAmount(order.transactionAmount());
        orderEntity.setCreationDate(order.creationDate());

        return orderEntity;
    }
}
