package com.example.payment_routing_engine.modules.transaction.dto.responses;

import com.example.payment_routing_engine.modules.transaction.dto.items.GatewayRecommendationItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {

    private GatewayRecommendationItem recommendedGateway;

    private List<GatewayRecommendationItem> alternatives;
}
