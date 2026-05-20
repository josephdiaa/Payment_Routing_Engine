package com.example.payment_routing_engine.modules.gateway.service.impl;

import com.example.payment_routing_engine.common.exception.BusinessValidationException;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.gateway.dto.requests.CreateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.responses.GatewayResponse;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.gateway.service.GatewayService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatewayServiceImpl implements GatewayService {

    GatewayRepository gatewayRepository;

    public GatewayServiceImpl(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    @Override
    public List<GatewayResponse> getAllGateways() {
        return gatewayRepository.findAll().stream().map(gateway -> GatewayResponse.fromEntity(gateway)).toList();
    }

    @Override
    public GatewayResponse createGateway(CreateGatewayRequest createGatewayRequest) {
        if(null!=createGatewayRequest.getMaxTransactionAmount()&& createGatewayRequest.getMaxTransactionAmount().compareTo(createGatewayRequest.getMinTransactionAmount()) <= 0){
            throw new BusinessValidationException("Max transaction amount must be greater than min transaction amount");
        }
        Gateway gateway = new Gateway();
        gateway.setActive(createGatewayRequest.getActive());
        gateway.setName(createGatewayRequest.getName());
        gateway.setAvailableDays(createGatewayRequest.getAvailableDays());
        gateway.setAvailabilityEnd(createGatewayRequest.getAvailabilityEnd());
        gateway.setDailyLimit(createGatewayRequest.getDailyLimit());
        gateway.setFixedCommission(createGatewayRequest.getFixedCommission());
        gateway.setAvailabilityStart(createGatewayRequest.getAvailabilityStart());
        gateway.setMaxTransactionAmount(createGatewayRequest.getMaxTransactionAmount());
        gateway.setMinTransactionAmount(createGatewayRequest.getMinTransactionAmount());
        gateway.setPercentageCommission(createGatewayRequest.getPercentageCommission());
        gateway.setProcessingTime(createGatewayRequest.getProcessingTime());
        gatewayRepository.saveAndFlush(gateway);
        return GatewayResponse.fromEntity(gateway);
    }
}
