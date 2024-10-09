package com.microservice_shopping.presentation.controller;

import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.presentation.dto.MessageDTO;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.presentation.dto.StatusOrderDTO;
import com.microservice_shopping.service.implementation.PurchaseOrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/shopping")
@Tag(name = "Shopping", description = "this is a microservice dedicate to manage all about the Purchases orders ")
public class PurchaseOrderController {

    PurchaseOrderServiceImpl purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderServiceImpl purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @Operation(
            summary = "Create a purchase order",
            description = "This endpoint is used to create a new purchase order",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The PurchaseOrderDTO contains the idOrder, idUser, emailUser, stateOrder, cart, transactionAmount, creationDate.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PurchaseOrderDTO.class
                            )
                    )
            ),
            tags = {"shopping", "purchase", "order"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful create",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = State.class
                                    )
                            )
                    )
            }
    )
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

    @Operation(
            summary = "Edit the status of an order ",
            description = "This endpoint is used to edit edits the status of an order that was pending at success",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The StatusOrderDTO contains the idPurchaseOrder, stateOrder.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = StatusOrderDTO.class
                            )
                    )
            ),
            tags = {"shopping", "purchase", "order", "PENDING", "SUCCESS"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful create",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = State.class
                                    )
                            )
                    )
            }
    )
    @PatchMapping(value = "/update-order")
    public ResponseEntity<MessageDTO<State>> updateOrderController(@RequestBody @Valid StatusOrderDTO statusOrderDTO) {
        State stateUpdate = purchaseOrderService.updateStatusPurchaseOrder(statusOrderDTO);
        if (stateUpdate == State.ERROR) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, "Error al actualizar la orden"));
        }
        return  ResponseEntity.ok(new MessageDTO<>(false,State.SUCCESS, "Se actualizó la orden correctamente, orden con estado: "));
    }

    @GetMapping(value = "/get-orders-byIdUser")
    @Operation(
            summary = " gets orders by user id",
            description = "This endpoint is used to get all orders of the a user, looking for them by their id",
            parameters = {
                    @Parameter(
                            name = "idUser",
                            description = "User's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )

            },
            tags = {"shopping", "purchase", "order", "SUCCESS"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful create",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<List<PurchaseOrderDTO>>> getOrdersByIdUserController(@RequestParam("idUser") String idUser) {
        List<PurchaseOrderDTO> orderDTOS = purchaseOrderService.getAllPurchaseOrdersByUserId(idUser);
        if (orderDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDTO<>(true,null,"No orders found for the given user ID"));
        }
        return ResponseEntity.ok(new MessageDTO<>(false,orderDTOS,"Se han encontrado los ordenes de compra correctamente"));
    }
}
