package com.microservice.cart.presentation.controller;

import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.persistence.model.vo.EventVO;
import com.microservice.cart.presentation.advice.CustomClientException;
import com.microservice.cart.presentation.dto.MessageDTO;
import com.microservice.cart.service.implementation.CartServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Manage Cart", description = "Private controller requiring authentication to access your endpoints")
public class CartController {

    final CartServiceImpl cartService;

    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    /**
     * This endpoint is used to run addToCart service
     * @param eventVO item for add to cart
     * @param idUser user's id
     * @return state action
     */
    @PostMapping(value = "/add-cart", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Add to cart",
            description = "Method for add items to cart of an user",
            tags = {"TRANSACTION"},
            parameters = {
                    @Parameter(
                            name = "idUser",
                            description = "user's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Item for add to cart",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = EventVO.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Create Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Create Event Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> addToCart(@RequestBody EventVO eventVO, @RequestParam String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, cartService.addToCart(idUser, eventVO)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/delete-to-cart")
    @Operation(
            summary = "Delete an item of the cart",
            description = "Delete an item of user's cart",
            tags = {"CRUD"},
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
                    ),
                    @Parameter(
                            name = "idEventVO",
                            description = "Item's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Create Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Create Event Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> deleteToCart(@RequestParam String idUser, @RequestParam String idEventVO) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, cartService.deleteToCart(idUser, idEventVO)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @PutMapping("/upgrade-quantity")
    @Operation(
            summary = "Upgrade quantity of an item of the cart",
            description = "Upgrade quantity item of user's cart",
            tags = {"CRUD"},
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
                    ),
                    @Parameter(
                            name = "idEventVO",
                            description = "Item's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "quantity",
                            description = "New quantity",
                            required = true,
                            content = @Content(
                                    mediaType = "Integer",
                                    schema = @Schema(
                                            implementation = Integer.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Create Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Create Event Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> upgradeQuantity(@RequestParam String idUser, @RequestParam String idEventVO, @RequestParam Integer quantity){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, cartService.upgradeQuantity(idUser, idEventVO, quantity)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/clear-cart")
    @Operation(
            summary = "Clear the cart",
            description = "Clear of user's cart",
            tags = {"CRUD"},
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
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Create Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Create Event Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> clearCart(@RequestParam String idUser) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, cartService.clearCart(idUser)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @GetMapping("/get-cart")
    @Operation(
            summary = "Get the cart",
            description = "Get of user's cart",
            tags = {"CRUD"},
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
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Create Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Create Event Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<List<EventVO>>> getCart(@RequestParam String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, cartService.getCart(idUser)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        }
    }
}
