package com.example.payment_routing_engine.modules.transaction.dto.responses;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SplitPaymentResponse {

    private String selectedGateway;

    private Boolean requiresSplitting;

    private List<BigDecimal> splits;

    private Integer splitCount;

    private BigDecimal totalCommission;

    private Boolean quotaAvailable;
}
