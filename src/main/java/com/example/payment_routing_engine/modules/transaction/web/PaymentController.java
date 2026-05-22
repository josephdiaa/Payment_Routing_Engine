package com.example.payment_routing_engine.modules.transaction.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import com.example.payment_routing_engine.modules.transaction.dto.requests.ProcessPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.requests.SplitPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.SplitPaymentResponse;
import com.example.payment_routing_engine.modules.transaction.service.PaymentProcessingService;
import com.example.payment_routing_engine.modules.transaction.service.PaymentSplitterService;
import com.example.payment_routing_engine.modules.transaction.service.RoutingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    RoutingService routingService;
    PaymentSplitterService paymentSplitterService;
    PaymentProcessingService paymentProcessingService;

    public PaymentController(RoutingService routingService,PaymentSplitterService paymentSplitterService,PaymentProcessingService paymentProcessingService) {
        this.routingService = routingService;
        this.paymentSplitterService = paymentSplitterService;
        this.paymentProcessingService=paymentProcessingService;
    }

    @PostMapping("/api/payments/recommend")
    public ResponseEntity<ApiResponse<RecommendationResponse>>recommendGateway(@RequestBody @Valid RecommendationRequest request){
        return ResponseEntity.ok(ApiResponse.ok(routingService.recommend(request),"Recommendation generated successfully"));
    }

    @PostMapping("/api/payments/split")
    public ResponseEntity<ApiResponse<SplitPaymentResponse>> splitPayment(@RequestBody @Valid SplitPaymentRequest request){
        return ResponseEntity.ok(ApiResponse.ok(paymentSplitterService.splitPayment(request)));
    }

    @PostMapping("/api/payments/process")
    public ResponseEntity<ApiResponse<String>> processPayment(@RequestBody @Valid ProcessPaymentRequest processPaymentRequest){
        return ResponseEntity.ok(ApiResponse.ok(paymentProcessingService.processPayment(processPaymentRequest)));
    }
}
