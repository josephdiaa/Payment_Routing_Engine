package com.example.payment_routing_engine.modules.gateway.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import com.example.payment_routing_engine.modules.gateway.dto.requests.CreateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.requests.UpdateGatewayRequest;
import com.example.payment_routing_engine.modules.gateway.dto.requests.UpdateGatewayStatusRequest;
import com.example.payment_routing_engine.modules.gateway.dto.responses.GatewayResponse;
import com.example.payment_routing_engine.modules.gateway.service.GatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PutMapping("/api/gateways/{gatewayId}")
    public ResponseEntity<ApiResponse<GatewayResponse>> updateGateway(@PathVariable UUID gatewayId, @RequestBody @Valid UpdateGatewayRequest updateGatewayRequest){
       return ResponseEntity.ok(ApiResponse.ok(gatewayService.updateGateway(gatewayId,updateGatewayRequest),"Gateway updated successfully"));
    }

    @PatchMapping("/api/gateways/{gatewayId}/status")
    public ResponseEntity<ApiResponse<GatewayResponse>> updateGatewayStatusRequest(@PathVariable UUID gatewayId, @RequestBody @Valid UpdateGatewayStatusRequest updateGatewayStatusRequest){
        return ResponseEntity.ok(ApiResponse.ok(gatewayService.updateGatewayStatus(gatewayId,updateGatewayStatusRequest), "Gateway status updated"));
    }
}
