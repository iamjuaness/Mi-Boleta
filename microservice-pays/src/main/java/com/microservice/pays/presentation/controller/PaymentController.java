package com.microservice.pays.presentation.controller;

import com.microservice.pays.presentation.dto.MessageDTO;
import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.implementation.PaymentServiceImpl;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    final PaymentServiceImpl paymentService;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/pay")
    public ResponseEntity<MessageDTO<PaymentResponse>>  pay(@RequestBody PaymentRequest paymentRequest, @RequestParam String strategyId) throws StripeException {
       PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest,strategyId);
   if (StringUtils.hasText(paymentResponse.url())) {
           return ResponseEntity.ok(new MessageDTO<>(false, paymentResponse));
       }
       return ResponseEntity.badRequest().body(new MessageDTO<>(true, paymentResponse));
    }
}
