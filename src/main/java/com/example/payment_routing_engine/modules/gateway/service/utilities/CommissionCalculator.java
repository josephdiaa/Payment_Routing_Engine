package com.example.payment_routing_engine.modules.gateway.service.utilities;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CommissionCalculator {
    public BigDecimal calculateCommission(BigDecimal amount,BigDecimal fixedFee,BigDecimal percentageFee){
        BigDecimal result = fixedFee.add((amount.multiply(percentageFee)).divide(BigDecimal.valueOf(100.0),RoundingMode.HALF_UP));
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
