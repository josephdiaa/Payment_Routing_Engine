package com.example.payment_routing_engine.modules.transaction.dto.responses;

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
public class GatewayBreakdownItem {
    private UUID gatewayId;
    private String gatewayName;
    private int transactionCount;
    private BigDecimal totalAmount;
    private BigDecimal totalCommission;
    private BigDecimal dailyLimit;
    private BigDecimal usedQuota;
    private BigDecimal remainingQuota;

}
