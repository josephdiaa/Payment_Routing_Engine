package com.example.payment_routing_engine.modules.gateway.service;

import com.example.payment_routing_engine.modules.gateway.dto.requests.CreateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.requests.UpdateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.responses.GatewayResponse;

import java.util.List;
import java.util.UUID;

public interface GatewayService {
    List<GatewayResponse> getAllGateways();
    GatewayResponse createGateway(CreateGatewayRequest createGatewayRequest);
    GatewayResponse updateGateway(UUID id, UpdateGatewayRequest updateGatewayRequest);
}
