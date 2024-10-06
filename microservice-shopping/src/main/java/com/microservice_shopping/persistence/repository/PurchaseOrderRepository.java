package com.microservice_shopping.persistence.repository;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {
}
