package com.example.payment_routing_engine.modules.gateway.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import com.example.payment_routing_engine.modules.gateway.dto.requests.CreateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.responses.GatewayResponse;
import com.example.payment_routing_engine.modules.gateway.service.GatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/api/gateways")
    public ResponseEntity<ApiResponse<GatewayResponse>> createGateway(@RequestBody @Valid CreateGatewayRequest createGatewayRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(gatewayService.createGateway(createGatewayRequest),"ok"));
    }
}
