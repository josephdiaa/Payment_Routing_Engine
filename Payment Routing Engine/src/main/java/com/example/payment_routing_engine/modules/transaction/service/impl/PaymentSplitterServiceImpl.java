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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @Transactional
    public SplitPaymentResponse splitPayment(SplitPaymentRequest request) {

        List<Gateway> gateways = gatewayRepository.findAll();

        Gateway selectedGateway = null;

        for (Gateway gateway : gateways) {

            if (!gateway.getActive()) {
                continue;
            }

            if (gateway.getMaxTransactionAmount() == null) {
                selectedGateway = gateway;
                break;
            }

            if (request.getAmount().compareTo(gateway.getMinTransactionAmount()) >= 0) {
                selectedGateway = gateway;
                break;
            }
        }

        if (selectedGateway == null) {
            throw new BusinessValidationException("No available gateway found for this request");
        }

        BigDecimal remainingQuota =
                gatewayDailyUsageService.getRemainingQuota(
                        selectedGateway.getId(),
                        LocalDate.now(),
                        selectedGateway.getDailyLimit()
                );

        boolean quotaAvailable =
                remainingQuota.compareTo(request.getAmount()) >= 0;

        BigDecimal maxTransactionAmount =
                selectedGateway.getMaxTransactionAmount();

        if (maxTransactionAmount == null ||
                request.getAmount().compareTo(maxTransactionAmount) <= 0) {

            List<BigDecimal> splits =
                    Collections.singletonList(request.getAmount());

            BigDecimal totalCommission =
                    commissionCalculator.calculateCommission(
                            request.getAmount(),
                            selectedGateway.getFixedCommission(),
                            selectedGateway.getPercentageCommission()
                    );

            return new SplitPaymentResponse(
                    selectedGateway.getName(),
                    false,
                    splits,
                    1,
                    totalCommission,
                    quotaAvailable
            );
        }

        List<BigDecimal> splits = new ArrayList<>();

        BigDecimal remainingAmount = request.getAmount();

        while (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {

            if (remainingAmount.compareTo(maxTransactionAmount) >= 0) {

                splits.add(maxTransactionAmount);

                remainingAmount = remainingAmount.subtract(maxTransactionAmount);

            } else {

                if (remainingAmount.compareTo(selectedGateway.getMinTransactionAmount()) < 0) {
                    throw new BusinessValidationException(
                            "Last split chunk is smaller than gateway minimum limit"
                    );
                }

                splits.add(remainingAmount);

                remainingAmount = BigDecimal.ZERO;
            }
        }

        BigDecimal totalCommission = BigDecimal.ZERO;

        for (BigDecimal split : splits) {
            totalCommission = totalCommission.add(
                    commissionCalculator.calculateCommission(
                            split,
                            selectedGateway.getFixedCommission(),
                            selectedGateway.getPercentageCommission()
                    )
            );
        }

        return new SplitPaymentResponse(
                selectedGateway.getName(),
                true,
                splits,
                splits.size(),
                totalCommission,
                quotaAvailable
        );
    }
}
