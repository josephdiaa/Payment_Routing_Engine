package com.example.payment_routing_engine.modules.gateway.service.impl;

import com.example.payment_routing_engine.modules.gateway.dto.GatewayResponse;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.gateway.service.GatewayService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatewayServiceInquireImpl implements GatewayService {

    GatewayRepository gatewayRepository;

    public GatewayServiceInquireImpl(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    @Override
    public List<GatewayResponse> getAllGateways() {
        return gatewayRepository.findAll().stream().map(gateway -> GatewayResponse.fromEntity(gateway)).toList();
    }
}
