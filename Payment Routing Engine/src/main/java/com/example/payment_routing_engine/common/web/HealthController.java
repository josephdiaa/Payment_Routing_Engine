package com.example.payment_routing_engine.common.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Object> checkHealth(){
        return  ApiResponse.ok("UP","ok");
    }
}
