package com.example.payment_routing_engine.modules.transaction.service.impl;

import com.example.payment_routing_engine.common.exception.BusinessValidationException;
import com.example.payment_routing_engine.common.exception.ResourceNotFoundException;
import com.example.payment_routing_engine.modules.biller.domain.Biller;
import com.example.payment_routing_engine.modules.biller.repository.BillerRepository;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.gateway.service.utilities.CommissionCalculator;
import com.example.payment_routing_engine.modules.gateway.service.utilities.GatewayAvailabilityChecker;
import com.example.payment_routing_engine.modules.gateway.service.utilities.GatewayValidator;
import com.example.payment_routing_engine.modules.transaction.dto.items.GatewayRecommendationItem;
import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;
import com.example.payment_routing_engine.modules.transaction.service.RoutingService;
import com.example.payment_routing_engine.modules.usage.service.GatewayDailyUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoutingServiceImpl implements RoutingService {
    private GatewayRepository gatewayRepository;
    private BillerRepository billerRepository;
    private GatewayAvailabilityChecker gatewayAvailabilityChecker;
    private GatewayValidator gatewayValidator;
    private GatewayDailyUsageService gatewayDailyUsageService;
    private CommissionCalculator commissionCalculator;


    public RoutingServiceImpl(GatewayRepository gatewayRepository, BillerRepository billerRepository, GatewayAvailabilityChecker gatewayAvailabilityChecker, GatewayValidator gatewayValidator, GatewayDailyUsageService gatewayDailyUsageService, CommissionCalculator commissionCalculator) {
        this.gatewayRepository = gatewayRepository;
        this.billerRepository = billerRepository;
        this.gatewayAvailabilityChecker = gatewayAvailabilityChecker;
        this.gatewayValidator = gatewayValidator;
        this.gatewayDailyUsageService = gatewayDailyUsageService;
        this.commissionCalculator = commissionCalculator;
    }

    @Override
    public RecommendationResponse recommend(RecommendationRequest request) {
        Optional<Biller> billerOptional = billerRepository.findByCode(request.getBillerId());
        if(billerOptional.isEmpty()){
            throw new ResourceNotFoundException("Biller not found with code: " + request.getBillerId());
        }
        List<Gateway> gatewayList = gatewayRepository.findAll();
        gatewayList = gatewayList.stream().
                filter(gateway -> gateway.getActive()==true).
                filter(gateway -> gatewayAvailabilityChecker.isAvailable(gateway, LocalDateTime.now())).
                filter(gateway -> gatewayValidator.isAmountValid(request.getAmount(),gateway)).
                filter(gateway -> gatewayDailyUsageService.getRemainingQuota(gateway.getId(), LocalDate.now(),gateway.getDailyLimit()).compareTo(request.getAmount())>=0).toList();

        if(gatewayList.isEmpty()){
            throw  new BusinessValidationException("No available gateway found for this request");
        }

        // Validate urgency value
        String urgency = request.getUrgency().toUpperCase();
        if (!urgency.equals("INSTANT") && !urgency.equals("CAN_WAIT")) {
            throw new BusinessValidationException("Invalid urgency. Use INSTANT or CAN_WAIT");
        }

        // Calculate commission for each viable gateway
        Map<Gateway, BigDecimal> commissionMap = new HashMap<>();
        for (Gateway gateway : gatewayList) {
            BigDecimal commission = commissionCalculator.calculateCommission(
                    request.getAmount(),
                    gateway.getFixedCommission(),
                    gateway.getPercentageCommission()
            );
            commissionMap.put(gateway, commission);
        }

        // Find the maximum commission (used to normalize cost scores)
        BigDecimal maxCommission = commissionMap.values().stream()
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ONE);

        // Score each gateway
        Map<Gateway, Double> scoreMap = new HashMap<>();
        for (Gateway gateway : gatewayList) {
            BigDecimal commission = commissionMap.get(gateway);

            // Cost score: cheaper = higher score
            double costScore = 100.0;
            if (maxCommission.compareTo(BigDecimal.ZERO) > 0) {
                costScore = 100.0 - (commission.divide(maxCommission, 10, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue());
            }

            // Speed score based on processing time
            double speedScore = getSpeedScore(gateway.getProcessingTime());

            // Final score weighted by urgency
            double finalScore;
            if (urgency.equals("INSTANT")) {
                finalScore = (speedScore * 0.70) + (costScore * 0.30);
            } else {
                finalScore = (speedScore * 0.30) + (costScore * 0.70);
            }
            scoreMap.put(gateway, finalScore);
        }

        // Sort gateways by score descending
        List<Gateway> sortedGateways = new ArrayList<>(gatewayList);
        sortedGateways.sort((g1, g2) -> Double.compare(scoreMap.get(g2), scoreMap.get(g1)));

        // Build response
        Gateway best = sortedGateways.get(0);
        GatewayRecommendationItem recommended =
                GatewayRecommendationItem.fromGateway(best, commissionMap.get(best));

        List<GatewayRecommendationItem> alternatives = new ArrayList<>();
        for (int i = 1; i < sortedGateways.size(); i++) {
            Gateway alt = sortedGateways.get(i);
            alternatives.add(GatewayRecommendationItem.fromGateway(alt, commissionMap.get(alt)));
        }

        return new RecommendationResponse(recommended, alternatives);
    }

    private int getSpeedScore(String processingTime){
        if(processingTime.equals("Instant")){
            return 100;
        }
        else if(processingTime.equals("2 hours")){
            return 60;
        }
        else if(processingTime.equals("24 hours")){
            return 20;
        }
        else{
            return 40;
        }
    }
}
