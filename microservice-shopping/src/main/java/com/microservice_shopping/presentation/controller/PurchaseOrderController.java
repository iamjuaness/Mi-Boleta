package com.microservice_shopping.presentation.controller;

import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.presentation.dto.MessageDTO;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.service.implementation.PurchaseOrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shopping")
public class PurchaseOrderController {

    PurchaseOrderServiceImpl purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderServiceImpl purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping(value = "/create-order")
    public ResponseEntity<MessageDTO<State>> createOrderController(@RequestBody @Valid PurchaseOrderDTO purchaseOrderDTO) {

        State stateCreate = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO);

        // Si la orden falla, devuelve un error
        if (stateCreate == State.ERROR) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, "Error al crear la orden"));
        }
        // Si la orden es exitosa, devuelve un mensaje de éxito
        return ResponseEntity.ok(new MessageDTO<>(false, State.SUCCESS, "Orden creada con éxito"));

    }
}
