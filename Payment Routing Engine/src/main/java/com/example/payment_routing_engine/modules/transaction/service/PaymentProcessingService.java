package com.example.payment_routing_engine.modules.transaction.service;

import com.example.payment_routing_engine.modules.transaction.dto.requests.ProcessPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.ProcessSplitPaymentResponse;

public interface PaymentProcessingService {
    public String processPayment(ProcessPaymentRequest request);
    public ProcessSplitPaymentResponse processSplitPayment(ProcessPaymentRequest request);
}
