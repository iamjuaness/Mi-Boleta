package com.microservice.pays.presentation.controller;

import com.microservice.pays.presentation.dto.MessageDTO;
import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.implementation.PaymentServiceImpl;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
@Tag(name = "msvc pays ", description = "this microservice is in charge of handling payments ")
@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    final PaymentServiceImpl paymentService;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(
            summary = "Pay",
            description = "this function is responsible for receiving a order and return the link of pay in stripe whit the information ",
            tags = {"pay", "cart", "transaction"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "paymentRequest contain  a order and the id of strategy pay  ",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PaymentRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return a link of pay in stripe checkout ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = PaymentResponse.class
                                    )
                            )

                    )
            }

    )
    @PostMapping(value = "/pay")
    public ResponseEntity<MessageDTO<PaymentResponse>>  pay(@RequestBody PaymentRequest paymentRequest) throws StripeException {
       PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);
   if (StringUtils.hasText(paymentResponse.url())) {
           return ResponseEntity.ok(new MessageDTO<>(false, paymentResponse));
       }
       return ResponseEntity.badRequest().body(new MessageDTO<>(true, paymentResponse));
    }

    @PatchMapping("/setPaymentStatus")
    public ResponseEntity<MessageDTO<String>> getPaymentStatus(@RequestParam("idSession") String idSession, @RequestParam("strategyId") String strategyId) throws StripeException {
        paymentService.setSateOrder(idSession, strategyId);
        return ResponseEntity.ok(new MessageDTO<>(true, "OK"));
    }
}
