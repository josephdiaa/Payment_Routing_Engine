package com.example.payment_routing_engine.modules.gateway.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGatewayRequest {
    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fixedCommission;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal percentageCommission;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal dailyLimit;

    @NotBlank
    private String processingTime;
    private LocalTime availabilityStart;
    private LocalTime availabilityEnd;

    @NotBlank
    private String availableDays;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal minTransactionAmount;


    private BigDecimal maxTransactionAmount;

    @NotNull
    private Boolean active;
}
