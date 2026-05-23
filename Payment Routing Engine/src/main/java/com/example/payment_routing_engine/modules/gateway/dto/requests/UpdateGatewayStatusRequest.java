package com.example.payment_routing_engine.modules.gateway.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGatewayStatusRequest {
    @NotNull
    private Boolean active;

}
