package com.example.payment_routing_engine.modules.gateway.service;

import com.example.payment_routing_engine.modules.gateway.dto.requests.CreateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.responses.GatewayResponse;

import java.util.List;

public interface GatewayService {
    List<GatewayResponse> getAllGateways();
    GatewayResponse createGateway(CreateGatewayRequest createGatewayRequest);
}
