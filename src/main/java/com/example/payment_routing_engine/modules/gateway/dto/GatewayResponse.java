package com.example.payment_routing_engine.modules.gateway.dto;

import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayResponse {
    private UUID id;
    private String name;
    private BigDecimal fixedCommission;
    private BigDecimal percentageCommission;
    private BigDecimal dailyLimit;
    private String processingTime;
    private LocalTime availabilityStart;
    private LocalTime availabilityEnd;
    private String availableDays;
    private BigDecimal minTransactionAmount;
    private BigDecimal maxTransactionAmount;
    private Boolean active;

    public static GatewayResponse fromEntity(Gateway gateway){
        GatewayResponse gatewayResponse=new GatewayResponse();
        gatewayResponse.setId(gateway.getId());
        gatewayResponse.setName(gateway.getName());
        gatewayResponse.setFixedCommission(gateway.getFixedCommission());
        gatewayResponse.setPercentageCommission(gateway.getPercentageCommission());
        gatewayResponse.setDailyLimit(gateway.getDailyLimit());
        gatewayResponse.setProcessingTime(gateway.getProcessingTime());
        gatewayResponse.setAvailabilityStart(gateway.getAvailabilityStart());
        gatewayResponse.setAvailabilityEnd(gateway.getAvailabilityEnd());
        gatewayResponse.setAvailableDays(gateway.getAvailableDays());
        gatewayResponse.setMinTransactionAmount(gateway.getMinTransactionAmount());
        gatewayResponse.setMaxTransactionAmount(gateway.getMaxTransactionAmount());
        gatewayResponse.setActive(gateway.getActive());
        return gatewayResponse;
    }
}
