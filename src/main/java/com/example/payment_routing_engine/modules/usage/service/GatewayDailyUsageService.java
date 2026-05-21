package com.example.payment_routing_engine.modules.usage.service;



import com.example.payment_routing_engine.modules.gateway.domain.Gateway;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public interface GatewayDailyUsageService {
    public BigDecimal getRemainingQuota(UUID gatewayId, LocalDate date,BigDecimal dailyLimit);
    public void updateUsage(UUID gatewayId, LocalDate date, Gateway gateway, BigDecimal amount);
}
