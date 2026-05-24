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

        Optional<Biller> billerOptional =
                billerRepository.findByCode(request.getBillerId());

        if (billerOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Biller not found with code: " + request.getBillerId()
            );
        }

        String urgency = request.getUrgency();

        if (urgency == null) {
            throw new BusinessValidationException("Urgency is required");
        }

        urgency = urgency.toUpperCase();

        if (!urgency.equals("INSTANT") && !urgency.equals("CAN_WAIT")) {
            throw new BusinessValidationException("Invalid urgency. Use INSTANT or CAN_WAIT");
        }

        List<Gateway> gatewayList = gatewayRepository.findAll();

        gatewayList = gatewayList.stream()
                .filter(gateway -> Boolean.TRUE.equals(gateway.getActive()))
                .filter(gateway ->
                        gatewayAvailabilityChecker.isAvailable(
                                gateway,
                                LocalDateTime.now()
                        )
                )
                .filter(gateway ->
                        gatewayValidator.isAmountValid(
                                request.getAmount(),
                                gateway
                        )
                )
                .filter(gateway ->
                        gatewayDailyUsageService.getRemainingQuota(
                                gateway.getId(),
                                LocalDate.now(),
                                gateway.getDailyLimit()
                        ).compareTo(request.getAmount()) >= 0
                )
                .toList();

        if (gatewayList.isEmpty()) {
            throw new BusinessValidationException("No available gateway found for this request");
        }

        Map<Gateway, BigDecimal> commissionMap = new HashMap<>();

        for (Gateway gateway : gatewayList) {

            BigDecimal commission =
                    commissionCalculator.calculateCommission(
                            request.getAmount(),
                            gateway.getFixedCommission(),
                            gateway.getPercentageCommission()
                    );

            commissionMap.put(gateway, commission);
        }

        BigDecimal minCommission = commissionMap.values()
                .stream()
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal maxCommission = commissionMap.values()
                .stream()
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ONE);

        BigDecimal commissionRange = maxCommission.subtract(minCommission);

        Map<Gateway, Double> scoreMap = new HashMap<>();

        for (Gateway gateway : gatewayList) {
            BigDecimal commission = commissionMap.get(gateway);
            double costScore;
            if (commissionRange.compareTo(BigDecimal.ZERO) == 0) {
                costScore = 100.0;

            } else {
                costScore = maxCommission
                        .subtract(commission)
                        .divide(commissionRange, 10, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            double speedScore = getSpeedScore(gateway.getProcessingTime());
            double finalScore;

            if (urgency.equals("INSTANT")) {
                finalScore = (speedScore * 0.80) + (costScore * 0.20);

            } else {
                finalScore = (speedScore * 0.10) + (costScore * 0.90);
            }
            scoreMap.put(gateway, finalScore);
        }

        List<Gateway> sortedGateways = new ArrayList<>(gatewayList);

        sortedGateways.sort((g1, g2) ->
                        Double.compare(scoreMap.get(g2), scoreMap.get(g1)));

        Gateway best = sortedGateways.get(0);

        GatewayRecommendationItem recommended =
                GatewayRecommendationItem.fromGateway(best, commissionMap.get(best));

        List<GatewayRecommendationItem> alternatives = new ArrayList<>();

        for (int i = 1; i < sortedGateways.size(); i++) {
            Gateway alternativeGateway = sortedGateways.get(i);
            alternatives.add(GatewayRecommendationItem.fromGateway(alternativeGateway, commissionMap.get(alternativeGateway))
            );
        }

        return new RecommendationResponse(
                recommended,
                alternatives
        );
    }

    private int getSpeedScore(String processingTime) {
        if ("Instant".equalsIgnoreCase(processingTime)) {
            return 100;
        }
        if ("2 hours".equalsIgnoreCase(processingTime)) {
            return 60;
        }
        if ("24 hours".equalsIgnoreCase(processingTime)) {
            return 20;
        }
        return 40;
    }
}
