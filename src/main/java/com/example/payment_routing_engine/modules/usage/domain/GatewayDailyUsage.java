package com.example.payment_routing_engine.modules.usage.domain;


import com.example.payment_routing_engine.common.model.BaseEntity;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gateway_daily_usage",
        uniqueConstraints = @UniqueConstraint(name = "uq_gateway_usage_date",
                columnNames = {"gateway_id", "usage_date"}))
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayDailyUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gateway_id", nullable = false)
    private Gateway gateway;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "transaction_count", nullable = false)
    private Integer transactionCount;
}
