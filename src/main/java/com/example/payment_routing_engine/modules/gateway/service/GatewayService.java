package com.example.payment_routing_engine.modules.gateway.service;

import com.example.payment_routing_engine.modules.gateway.dto.GatewayResponse;

import java.util.List;

public interface GatewayService {
    List<GatewayResponse> getAllGateways();
}
