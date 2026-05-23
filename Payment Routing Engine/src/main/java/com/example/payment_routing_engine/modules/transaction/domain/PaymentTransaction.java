package com.example.payment_routing_engine.modules.transaction.domain;

import com.example.payment_routing_engine.common.model.BaseEntity;
import com.example.payment_routing_engine.modules.biller.domain.Biller;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction extends BaseEntity {

    @Column(name = "reference_number",nullable = false,unique = true)
    private String referenceNumber;

    @Column(name = "amount",nullable = false)
    private BigDecimal amount;

    @Column(name = "commission_amount",nullable = false)
    private BigDecimal commissionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biller_id",nullable = false)
    private Biller biller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gateway_id",nullable = false)
    private Gateway gateway;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

}
