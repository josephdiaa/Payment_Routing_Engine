package com.example.payment_routing_engine.modules.gateway.service.impl;

import com.example.payment_routing_engine.common.exception.BusinessValidationException;
import com.example.payment_routing_engine.common.exception.ResourceNotFoundException;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.gateway.dto.requests.CreateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.requests.UpdateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.responses.GatewayResponse;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.gateway.service.GatewayService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public GatewayResponse updateGateway(UUID id,UpdateGatewayRequest updateGatewayRequest) {
        Optional<Gateway> gatewayOp = gatewayRepository.findById(id);
        if(gatewayOp.isEmpty()){
            throw new ResourceNotFoundException("Gateway not found with id: " + id);
        }

        if(null!=updateGatewayRequest.getMaxTransactionAmount()&& updateGatewayRequest.getMaxTransactionAmount().compareTo(updateGatewayRequest.getMinTransactionAmount()) <= 0){
            throw new BusinessValidationException("Max transaction amount must be greater than min transaction amount");
        }
        Gateway gatewayEntity=gatewayOp.get();
        gatewayEntity.setActive(updateGatewayRequest.getActive());
        gatewayEntity.setName(updateGatewayRequest.getName());
        gatewayEntity.setAvailableDays(updateGatewayRequest.getAvailableDays());
        gatewayEntity.setAvailabilityEnd(updateGatewayRequest.getAvailabilityEnd());
        gatewayEntity.setDailyLimit(updateGatewayRequest.getDailyLimit());
        gatewayEntity.setFixedCommission(updateGatewayRequest.getFixedCommission());
        gatewayEntity.setAvailabilityStart(updateGatewayRequest.getAvailabilityStart());
        gatewayEntity.setMaxTransactionAmount(updateGatewayRequest.getMaxTransactionAmount());
        gatewayEntity.setMinTransactionAmount(updateGatewayRequest.getMinTransactionAmount());
        gatewayEntity.setPercentageCommission(updateGatewayRequest.getPercentageCommission());
        gatewayEntity.setProcessingTime(updateGatewayRequest.getProcessingTime());
        gatewayRepository.saveAndFlush(gatewayEntity);
        return GatewayResponse.fromEntity(gatewayEntity);
    }
}
