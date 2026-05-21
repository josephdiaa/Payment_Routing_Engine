package com.example.payment_routing_engine.modules.transaction.service.impl;

import com.example.payment_routing_engine.common.exception.BusinessValidationException;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.gateway.service.utilities.CommissionCalculator;
import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.requests.SplitPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.SplitPaymentResponse;
import com.example.payment_routing_engine.modules.transaction.service.PaymentSplitterService;
import com.example.payment_routing_engine.modules.transaction.service.RoutingService;
import com.example.payment_routing_engine.modules.usage.service.GatewayDailyUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class PaymentSplitterServiceImpl implements PaymentSplitterService {
    private RoutingService routingService;
    private CommissionCalculator commissionCalculator;
    private GatewayDailyUsageService gatewayDailyUsageService;
    private GatewayRepository gatewayRepository;

    public PaymentSplitterServiceImpl(RoutingService routingService, GatewayRepository gatewayRepository, GatewayDailyUsageService gatewayDailyUsageService, CommissionCalculator commissionCalculator) {
        this.routingService = routingService;
        this.gatewayRepository = gatewayRepository;
        this.gatewayDailyUsageService = gatewayDailyUsageService;
        this.commissionCalculator = commissionCalculator;
    }

    @Override
    public SplitPaymentResponse splitPayment(SplitPaymentRequest request) {
        RecommendationRequest recommendationRequest = new RecommendationRequest(request.getBillerId(), request.getAmount(), request.getUrgency());
        RecommendationResponse recommendationResponse = routingService.recommend(recommendationRequest);
        Gateway gateway = gatewayRepository.getReferenceById(recommendationResponse.getRecommendedGateway().getId());

        if(null==gateway.getMaxTransactionAmount() || request.getAmount().compareTo(gateway.getMaxTransactionAmount())<=0 ){
            List<BigDecimal> splits= Collections.singletonList(request.getAmount());
            return new SplitPaymentResponse(gateway.getName(),false,splits,1,recommendationResponse.getRecommendedGateway().getEstimatedCommission(),true);
        }
        else{
            List<BigDecimal> splits = List.of();
            BigDecimal remainingAmount =request.getAmount();
            while(remainingAmount.compareTo(BigDecimal.ZERO)>0){
                if(remainingAmount.compareTo(gateway.getMaxTransactionAmount())>=0){
                    splits.add(gateway.getMaxTransactionAmount());
                    remainingAmount.subtract(gateway.getMaxTransactionAmount());
                }
                else{
                    if(remainingAmount.compareTo(gateway.getMinTransactionAmount())<0){
                        throw new BusinessValidationException("Last split chunk is smaller than gateway minimum limit");
                    }
                    splits.add(remainingAmount);
                    remainingAmount=BigDecimal.ZERO;
                }
            }

        }
        return null;
    }
}
