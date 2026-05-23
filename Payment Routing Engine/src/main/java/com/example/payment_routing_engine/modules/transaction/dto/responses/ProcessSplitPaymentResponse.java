package com.example.payment_routing_engine.modules.transaction.dto.responses;

import com.example.payment_routing_engine.modules.transaction.dto.responses.items.SplitPaymentTransactionItem;
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
public class ProcessSplitPaymentResponse {

    private String mainReferenceNumber;

    private String selectedGateway;

    private Boolean requiresSplitting;

    private Integer splitCount;

    private BigDecimal totalAmount;

    private BigDecimal totalCommission;

    private List<SplitPaymentTransactionItem> transactions;
}
