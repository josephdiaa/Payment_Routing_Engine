package com.example.payment_routing_engine.modules.usage.service.impl;

import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.usage.domain.GatewayDailyUsage;
import com.example.payment_routing_engine.modules.usage.repository.GatewayDailyUsageRepository;
import com.example.payment_routing_engine.modules.usage.service.GatewayDailyUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class GatewayDailyUsageServiceImpl implements GatewayDailyUsageService {
    GatewayDailyUsageRepository gatewayDailyUsageRepository;

    public GatewayDailyUsageServiceImpl(GatewayDailyUsageRepository gatewayDailyUsageRepository) {
        this.gatewayDailyUsageRepository = gatewayDailyUsageRepository;
    }

    @Override
    public BigDecimal getRemainingQuota(UUID gatewayId, LocalDate date, BigDecimal dailyLimit) {
        Optional<GatewayDailyUsage> gatewayDailyUsage = gatewayDailyUsageRepository.findByGatewayIdAndUsageDate(gatewayId,date);
        if(gatewayDailyUsage.isEmpty()){
            return dailyLimit;
        }
        else{
            GatewayDailyUsage gatewayDailyUsageEntity = gatewayDailyUsage.get();
            return dailyLimit.subtract(gatewayDailyUsageEntity.getTotalAmount());
        }

    }

    public void updateUsage(UUID gatewayId, LocalDate date,Gateway gateway,BigDecimal amount){
        Optional<GatewayDailyUsage> gatewayDailyUsage = gatewayDailyUsageRepository.findByGatewayIdAndUsageDate(gatewayId,date);
        if(gatewayDailyUsage.isEmpty()){
            GatewayDailyUsage newGatewayDailyUsage = new GatewayDailyUsage(gateway,date,amount,1);
            gatewayDailyUsageRepository.saveAndFlush(newGatewayDailyUsage);
        }
        else{
            GatewayDailyUsage gatewayDailyUsageEntity = gatewayDailyUsage.get();
            gatewayDailyUsageEntity.setTotalAmount(gatewayDailyUsageEntity.getTotalAmount().add(amount));
            gatewayDailyUsageEntity.setTransactionCount(gatewayDailyUsageEntity.getTransactionCount()+1);
            gatewayDailyUsageRepository.saveAndFlush(gatewayDailyUsageEntity);
        }
    }
}
