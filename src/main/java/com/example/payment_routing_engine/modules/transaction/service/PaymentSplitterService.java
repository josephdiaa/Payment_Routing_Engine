package com.example.payment_routing_engine.modules.transaction.service;

import com.example.payment_routing_engine.modules.transaction.dto.requests.SplitPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.SplitPaymentResponse;

public interface PaymentSplitterService {
    public SplitPaymentResponse splitPayment(SplitPaymentRequest request);
}
