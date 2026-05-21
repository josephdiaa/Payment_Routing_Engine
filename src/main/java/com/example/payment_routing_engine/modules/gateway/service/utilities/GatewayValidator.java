package com.example.payment_routing_engine.modules.gateway.service.utilities;

import com.example.payment_routing_engine.common.exception.BusinessValidationException;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GatewayValidator {
    public Boolean isAmountValid(BigDecimal amount, Gateway gateway){
        if(amount==null || amount.compareTo(BigDecimal.valueOf(0))<=0){
            throw new BusinessValidationException("Amount must be greater than zero");
        }

        if(amount.compareTo(gateway.getMinTransactionAmount())<0){
            return false;
        }

        if(gateway.getMaxTransactionAmount() != null && amount.compareTo(gateway.getMaxTransactionAmount())>0){
            return false;
        }
        return true;
    }
}
