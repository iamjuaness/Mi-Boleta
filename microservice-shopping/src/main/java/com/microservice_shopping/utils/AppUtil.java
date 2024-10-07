package com.microservice_shopping.utils;

import com.microservice_shopping.persistence.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class AppUtil  {

    PurchaseOrderRepository purchaseOrderRepository;
    public AppUtil(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public boolean checkOrderExists(String orderId) {
        return purchaseOrderRepository.existsById(orderId);
    }
}
