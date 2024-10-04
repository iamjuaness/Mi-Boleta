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
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = "/pay")
    public ResponseEntity<MessageDTO<PaymentResponse>>  pay(@RequestBody PaymentRequest paymentRequest) throws StripeException {
       PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);
   if (StringUtils.hasText(paymentResponse.url())) {
           return ResponseEntity.ok(new MessageDTO<>(false, paymentResponse));
       }
       return ResponseEntity.badRequest().body(new MessageDTO<>(true, paymentResponse));
    }
}
