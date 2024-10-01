package com.microservice.pays.presentation.controller;

import com.microservice.pays.presentation.dto.MessageDTO;
import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.implementation.PaymentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    final PaymentServiceImpl paymentService;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/pay")
    public ResponseEntity<MessageDTO<PaymentResponse>>  pay(@RequestBody PaymentRequest paymentRequest, @RequestParam String strategyId) {
       PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest,strategyId);
       if (paymentResponse.status() != "error") {
           return ResponseEntity.ok(new MessageDTO<>(false, paymentResponse));
       }
       return ResponseEntity.badRequest().body(new MessageDTO<>(true, paymentResponse));
    }
}
