package com.microservice.pays.service.interfaces;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;

public interface PaymentStrategy {

    /**
     * Método para procesar un pago basado en el proveedor de pagos.
     * @param request Datos del pago como monto, token, descripción, etc.
     * @return Un objeto PaymentResponse con el resultado del procesamiento.
     */
    PaymentResponse processPayment(PaymentRequest request);

    // Validar la información del pago
    boolean validatePaymentDetails(String paymentDetails);

    // Método para reembolsar pagos
    boolean refundPayment(String paymentId, double amount);

    // Verificar el estado de un pago
    String checkPaymentStatus(String paymentId);

    // Obtener el recibo del pago
    String getPaymentReceipt(String paymentId);

    // Cancelar un pago antes de completarse
    boolean cancelPayment(String paymentId);
}
