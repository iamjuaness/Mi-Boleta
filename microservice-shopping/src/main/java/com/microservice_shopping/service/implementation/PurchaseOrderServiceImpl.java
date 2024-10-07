package com.microservice_shopping.service.implementation;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.persistence.repository.PurchaseOrderRepository;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.service.interfaces.PurchaseOrderService;
import com.microservice_shopping.utils.mapper.MapperOrder;
import com.mongodb.MongoException;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    MapperOrder mapperOrder;
    PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, MapperOrder mapperOrder) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.mapperOrder = mapperOrder;
    }

    @Override
    public State createPurchaseOrder(PurchaseOrderDTO purchaseOrder) {
      try {
          if (purchaseOrder == null) {
              throw new IllegalArgumentException("Purchase order cannot be null");
          }
          if (purchaseOrder.cart() == null) {
              throw new IllegalArgumentException("Cart cannot be null");
          }
          //pasar de un purchase order DTO a una entidad
          PurchaseOrder newPurchaseOrder = new PurchaseOrder(mapperOrder.dtoOrderToEntity(purchaseOrder));

          //guardar en la base de datos la entidad de purchase order
          purchaseOrderRepository.save(newPurchaseOrder);

          //retornar un estado satisfactorio
          return State.SUCCESS;
      }catch (MongoException e) {
          return State.ERROR;
      }

    }
}
