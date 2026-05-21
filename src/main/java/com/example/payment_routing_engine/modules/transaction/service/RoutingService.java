package com.example.payment_routing_engine.modules.transaction.service;

import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;

public interface RoutingService {
    public RecommendationResponse recommend(RecommendationRequest request);
}
