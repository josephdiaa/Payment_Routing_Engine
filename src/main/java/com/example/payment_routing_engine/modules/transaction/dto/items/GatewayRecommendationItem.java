package com.example.payment_routing_engine.modules.transaction.dto.items;

import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRecommendationItem {

    private UUID id;

    private String name;

    private BigDecimal estimatedCommission;

    private String processingTime;

    public static GatewayRecommendationItem fromGateway(Gateway gateway,BigDecimal commission){
        return new GatewayRecommendationItem(gateway.getId(),gateway.getName(),commission,gateway.getProcessingTime());
    }
}
