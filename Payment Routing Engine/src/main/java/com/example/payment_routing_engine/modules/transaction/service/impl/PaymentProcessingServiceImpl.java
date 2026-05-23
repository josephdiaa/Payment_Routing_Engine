package com.example.payment_routing_engine.modules.transaction.service.impl;

import com.example.payment_routing_engine.common.exception.ResourceNotFoundException;
import com.example.payment_routing_engine.modules.biller.domain.Biller;
import com.example.payment_routing_engine.modules.biller.repository.BillerRepository;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.transaction.domain.PaymentTransaction;
import com.example.payment_routing_engine.modules.transaction.domain.TransactionStatus;
import com.example.payment_routing_engine.modules.transaction.dto.requests.ProcessPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;
import com.example.payment_routing_engine.modules.transaction.repository.PaymentTransactionRepository;
import com.example.payment_routing_engine.modules.transaction.service.PaymentProcessingService;
import com.example.payment_routing_engine.modules.transaction.service.RoutingService;
import com.example.payment_routing_engine.modules.usage.service.GatewayDailyUsageService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private RoutingService routingService;
    private BillerRepository billerRepository;
    private GatewayRepository gatewayRepository;
    private GatewayDailyUsageService gatewayDailyUsageService;
    private PaymentTransactionRepository paymentTransactionRepository;

    public PaymentProcessingServiceImpl(RoutingService routingService, BillerRepository billerRepository, GatewayRepository gatewayRepository, GatewayDailyUsageService gatewayDailyUsageService, PaymentTransactionRepository paymentTransactionRepository) {
        this.routingService = routingService;
        this.billerRepository = billerRepository;
        this.gatewayRepository = gatewayRepository;
        this.gatewayDailyUsageService = gatewayDailyUsageService;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public String processPayment(ProcessPaymentRequest request) {

        Optional<Biller> billerOptional = billerRepository.findByCode(request.getBillerId());

        if (billerOptional.isEmpty()) {
            throw new ResourceNotFoundException("Biller not found with code: " + request.getBillerId());
        }
        Biller biller = billerOptional.get();
        RecommendationRequest recommendationRequest = new RecommendationRequest(request.getBillerId(), request.getAmount(), request.getUrgency());
        RecommendationResponse recommendationResponse = routingService.recommend(recommendationRequest);
        Gateway gateway = gatewayRepository.getReferenceById(recommendationResponse.getRecommendedGateway().getId());
        gatewayDailyUsageService.updateUsage(gateway.getId(), LocalDate.now(), gateway, request.getAmount());
        PaymentTransaction paymentTransaction = new PaymentTransaction(UUID.randomUUID().toString(), request.getAmount(), recommendationResponse.getRecommendedGateway().getEstimatedCommission(), TransactionStatus.SUCCESS, biller, gateway, LocalDateTime.now());
        paymentTransactionRepository.saveAndFlush(paymentTransaction);
        return paymentTransaction.getReferenceNumber();
    }
}
