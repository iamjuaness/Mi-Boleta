package com.microservice.pays;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.implementation.PaymentServiceImpl;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import com.microservice.pays.utils.PaymentStrategyFactory;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private PaymentServiceImpl paymentService;  // Suponiendo que tu clase de servicio se llama PaymentServiceImpl

    private PaymentRequest mockPaymentRequest;
    private PaymentResponse mockPaymentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock de PaymentRequest y PaymentResponse
        mockPaymentRequest = mock(PaymentRequest.class);
        mockPaymentResponse = mock(PaymentResponse.class);
    }

    @Test
    void createPayment_success() throws StripeException {
        // Arrange
        when(mockPaymentRequest.strategyId()).thenReturn("stripe");
        when(paymentStrategyFactory.getStrategy("stripe")).thenReturn(paymentStrategy);
        when(paymentStrategy.processPayment(mockPaymentRequest)).thenReturn(mockPaymentResponse);

        // Act
        PaymentResponse result = paymentService.createPayment(mockPaymentRequest);

        // Assert
        assertEquals(mockPaymentResponse, result);
        verify(paymentStrategyFactory).getStrategy("stripe");
        verify(paymentStrategy).processPayment(mockPaymentRequest);
    }

    @Test
    void createPayment_paymentRequestIsNull_throwsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(null), "PaymentRequest cannot be null or empty");
        verifyNoInteractions(paymentStrategyFactory);  // Asegurarse de que no se llama a la fábrica si el request es null
    }



}
