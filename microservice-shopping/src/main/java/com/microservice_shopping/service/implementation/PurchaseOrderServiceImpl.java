package com.microservice_shopping.service.implementation;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.service.interfaces.PurchaseOrderService;
import com.microservice_shopping.utils.mapper.MapperOrder;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    MapperOrder mapperOrder = new MapperOrder();

    @Override
    public State createPurchaseOrder(PurchaseOrderDTO purchaseOrder) {
        if (purchaseOrder == null) {
            throw new IllegalArgumentException("Purchase order cannot be null");
        }
        if (purchaseOrder.cart() == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }


        PurchaseOrder newPurchaseOrder = new PurchaseOrder(mapperOrder.dtoOrderToEntity(purchaseOrder));
        return State.SUCCESS;

    }
}
