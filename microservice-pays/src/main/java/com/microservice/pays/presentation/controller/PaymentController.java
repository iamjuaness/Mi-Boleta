package com.microservice.pays.presentation.controller;

import com.microservice.pays.service.implementation.PaymentServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    PaymentServiceImpl paymentService;
    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }
}
