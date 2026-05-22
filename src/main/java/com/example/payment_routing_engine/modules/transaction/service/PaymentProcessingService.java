package com.example.payment_routing_engine.modules.transaction.service;

import com.example.payment_routing_engine.modules.transaction.dto.requests.ProcessPaymentRequest;

public interface PaymentProcessingService {
    public String processPayment(ProcessPaymentRequest request);
}
