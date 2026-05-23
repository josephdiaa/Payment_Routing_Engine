package com.example.payment_routing_engine.modules.transaction.dto.responses.items;

import com.example.payment_routing_engine.modules.transaction.domain.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SplitPaymentTransactionItem {

    private String referenceNumber;


    private BigDecimal amount;


    private BigDecimal commission;

    private TransactionStatus status;
}
