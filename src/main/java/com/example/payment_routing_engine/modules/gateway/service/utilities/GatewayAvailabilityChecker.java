package com.example.payment_routing_engine.modules.gateway.service.utilities;

import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
public class GatewayAvailabilityChecker {
    public Boolean isAvailable(Gateway gateway, LocalDateTime requestTime){
        if(gateway.getAvailabilityStart()==null && gateway.getAvailabilityEnd()==null){
            return true;
        }
        String day=requestTime.getDayOfWeek().name().substring(0,3);
        String gatewayDays=gateway.getAvailableDays();
        List<String> days= Arrays.stream(gatewayDays.split(",")).toList();
        if(!days.contains(day)){
            return false;
        }
        LocalTime reqTime=requestTime.toLocalTime();

        if(gateway.getAvailabilityStart().isAfter(reqTime)||gateway.getAvailabilityEnd().isBefore(reqTime)){
            return false;
        }
        return true;
    }
}
