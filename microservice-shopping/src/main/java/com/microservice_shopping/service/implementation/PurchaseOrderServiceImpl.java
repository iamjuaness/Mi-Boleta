package com.microservice_shopping.service.implementation;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.persistence.repository.PurchaseOrderRepository;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.presentation.dto.StatusOrderDTO;
import com.microservice_shopping.service.interfaces.PurchaseOrderService;
import com.microservice_shopping.utils.AppUtil;
import com.microservice_shopping.utils.mapper.MapperOrder;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    MapperOrder mapperOrder;
    PurchaseOrderRepository purchaseOrderRepository;
    AppUtil appUtil;
    MongoTemplate mongoTemplate;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, MapperOrder mapperOrder, AppUtil appUtil,
                                    MongoTemplate mongoTemplate) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.mapperOrder = mapperOrder;
        this.appUtil = appUtil;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public State createPurchaseOrder(PurchaseOrderDTO purchaseOrder) {
      try {


          //verificar si la información es nula
          if (purchaseOrder == null) {
              throw new IllegalArgumentException("Purchase order cannot be null");
          }
          if (purchaseOrder.cart() == null) {
              throw new IllegalArgumentException("Cart cannot be null");
          }

          //verificar si lo orden de compra ya existe
           if (appUtil.checkOrderExists(purchaseOrder.idOrder())) throw new IllegalArgumentException("Order already exists");

          //pasar de un purchase order DTO a una entidad
          PurchaseOrder newPurchaseOrder = mapperOrder.dtoOrderToEntity(purchaseOrder);

          //guardar en la base de datos la entidad de purchase order
          purchaseOrderRepository.save(newPurchaseOrder);

          //retornar un estado satisfactorio
          return State.SUCCESS;
      }catch (MongoException e) {
          return State.ERROR;
      }

    }

    @Override
    public State updateStatusPurchaseOrder(StatusOrderDTO statusOrderDTO) {
        try {
            if (statusOrderDTO == null) {
                throw new IllegalArgumentException("Status order cannot be null");
            }
            if (!appUtil.checkOrderExists(statusOrderDTO.idPurchaseOrder())) throw new IllegalArgumentException("la orden no existe");

            //create a query to find a purchase order by id
            Query query = new Query();
            query.addCriteria(Criteria.where("idOrder").is(statusOrderDTO.idPurchaseOrder()));

            // Create an update to set the state
            Update update = new Update().set("stateOrder",statusOrderDTO.stateOrder());

            // Perform the update operation
            UpdateResult result = mongoTemplate.updateFirst(query, update, PurchaseOrder.class);

            // Check if the state was updated
            if (result.getModifiedCount() == 0) {
                throw new IllegalArgumentException("Failed to update purchase order");
            }

            return State.SUCCESS;

        }catch (MongoException e) {
            return State.ERROR;
        }

    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrdersByUserId(String userId) {

          if (userId == null) {
              throw new IllegalArgumentException("User id cannot be null");
          }
          List<PurchaseOrderDTO> purchaseOrderDTOS = purchaseOrderRepository.findByUserId(userId);

          if (purchaseOrderDTOS == null || purchaseOrderDTOS.isEmpty()) {
              throw new IllegalArgumentException("No purchaseOrder found");
          }

          return purchaseOrderDTOS;

    }

}
