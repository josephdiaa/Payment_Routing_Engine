package com.example.payment_routing_engine.modules.transaction.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyTransactionSummaryResponse {
    private BigDecimal totalProcessedAmount;
    private BigDecimal totalCommissionCharged;
    private List<GatewayBreakdownItem> breakdown;
}
