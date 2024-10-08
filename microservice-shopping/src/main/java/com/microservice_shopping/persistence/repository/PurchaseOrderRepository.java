package com.microservice_shopping.persistence.repository;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {

    @Aggregation(pipeline = {
            "{ $match: { idUser: ?0, stateOrder: 'SUCCESS' } }",
            "{ $project: { " +
                    "    idOrder: 1, " +                // Proyectamos idOrder
                    "    idUser: 1, " +                 // Proyectamos idUser
                    "    emailUser: 1, " +              // Proyectamos emailUser
                    "    stateOrder: 1, " +             // Proyectamos stateOrder
                    "    cart: 1, " +                   // Proyectamos cart (lista de EventVO)
                    "    transactionAmount: 1, " +      // Proyectamos transactionAmount
                    "    creationDate: 1 " +            // Proyectamos creationDate
                    "} }"

    })
    List<PurchaseOrderDTO> findByUserId(String idUser);
}
