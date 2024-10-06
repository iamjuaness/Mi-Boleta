package com.microservice_shopping.service.interfaces;


import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;

public interface PurchaseOrderService {

    State createPurchaseOrder(PurchaseOrderDTO purchaseOrder);
}
