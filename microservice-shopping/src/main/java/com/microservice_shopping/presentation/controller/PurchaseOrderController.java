package com.microservice_shopping.presentation.controller;

import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.presentation.dto.MessageDTO;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.presentation.dto.StatusOrderDTO;
import com.microservice_shopping.service.implementation.PurchaseOrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PatchMapping(value = "/update-order")
    public ResponseEntity<MessageDTO<State>> updateOrderController(@RequestBody @Valid StatusOrderDTO statusOrderDTO) {
        State stateUpdate = purchaseOrderService.updateStatusPurchaseOrder(statusOrderDTO);
        if (stateUpdate == State.ERROR) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, "Error al actualizar la orden"));
        }
        return  ResponseEntity.ok(new MessageDTO<>(false,State.SUCCESS, "Se actualizó la orden correctamente, orden con estado: "));
    }

    @GetMapping(value = "/get-orders-byIdUser")
    public ResponseEntity<MessageDTO<List<PurchaseOrderDTO>>> getOrdersByIdUserController(@RequestParam("idUser") String idUser) {
        List<PurchaseOrderDTO> orderDTOS = purchaseOrderService.getAllPurchaseOrdersByUserId(idUser);
        if (orderDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDTO<>(true,null,"No orders found for the given user ID"));
        }
        return ResponseEntity.ok(new MessageDTO<>(false,orderDTOS,"Se han encontrado los ordenes de compra correctamente"));
    }
}
