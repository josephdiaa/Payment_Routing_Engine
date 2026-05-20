package com.example.payment_routing_engine.modules.gateway.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import com.example.payment_routing_engine.modules.gateway.dto.GatewayResponse;
import com.example.payment_routing_engine.modules.gateway.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class GatewayController {

    GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/api/gateways")
    public ApiResponse<List<GatewayResponse>> getAllGateways(){
        return ApiResponse.ok(gatewayService.getAllGateways());
    }
}
