package com.example.payment_routing_engine.modules.gateway.domain;

import com.example.payment_routing_engine.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "gateways")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gateway extends BaseEntity {

    @Column(name = "name",nullable = false,unique = true)
    private String name;

    @Column(name = "fixed_commission",nullable = false)
    private BigDecimal fixedCommission;

    @Column(name = "percentage_commission",nullable = false)
    private BigDecimal percentageCommission;

    @Column(name = "daily_limit",nullable = false)
    private BigDecimal dailyLimit;

    @Column(name = "processing_time",nullable = false)
    private String processingTime;

    @Column(name = "availability_start")
    private LocalTime availabilityStart;

    @Column(name = "availability_end")
    private LocalTime  availabilityEnd;

    @Column(name = "available_days",nullable = false)
    private String availableDays;

    @Column(name = "min_transaction_amount", nullable = false)
    private BigDecimal minTransactionAmount;

    @Column(name = "max_transaction_amount")
    private BigDecimal maxTransactionAmount;

    @Column(name = "active",nullable = false)
    private Boolean active;

}
