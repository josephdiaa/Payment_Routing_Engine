package com.example.payment_routing_engine.modules.transaction.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;
import com.example.payment_routing_engine.modules.transaction.service.RoutingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    RoutingService routingService;

    public PaymentController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @PostMapping("/api/payments/recommend")
    public ResponseEntity<ApiResponse<RecommendationResponse>>recommendGateway(@RequestBody @Valid RecommendationRequest request){
        return ResponseEntity.ok(ApiResponse.ok(routingService.recommend(request),"Recommendation generated successfully"));
    }
}
