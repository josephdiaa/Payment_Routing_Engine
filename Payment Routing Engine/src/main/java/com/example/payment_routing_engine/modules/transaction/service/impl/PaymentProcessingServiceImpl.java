package com.example.payment_routing_engine.modules.transaction.service.impl;

import com.example.payment_routing_engine.common.exception.BusinessValidationException;
import com.example.payment_routing_engine.common.exception.ResourceNotFoundException;
import com.example.payment_routing_engine.modules.biller.domain.Biller;
import com.example.payment_routing_engine.modules.biller.repository.BillerRepository;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.gateway.repository.GatewayRepository;
import com.example.payment_routing_engine.modules.gateway.service.utilities.CommissionCalculator;
import com.example.payment_routing_engine.modules.transaction.domain.PaymentTransaction;
import com.example.payment_routing_engine.modules.transaction.domain.TransactionStatus;
import com.example.payment_routing_engine.modules.transaction.dto.requests.ProcessPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.requests.RecommendationRequest;
import com.example.payment_routing_engine.modules.transaction.dto.requests.SplitPaymentRequest;
import com.example.payment_routing_engine.modules.transaction.dto.responses.ProcessSplitPaymentResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.RecommendationResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.SplitPaymentResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.items.SplitPaymentTransactionItem;
import com.example.payment_routing_engine.modules.transaction.repository.PaymentTransactionRepository;
import com.example.payment_routing_engine.modules.transaction.service.PaymentProcessingService;
import com.example.payment_routing_engine.modules.transaction.service.PaymentSplitterService;
import com.example.payment_routing_engine.modules.transaction.service.RoutingService;
import com.example.payment_routing_engine.modules.usage.service.GatewayDailyUsageService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private  PaymentSplitterService paymentSplitterService;
    private  CommissionCalculator commissionCalculator;

    public PaymentProcessingServiceImpl(RoutingService routingService, BillerRepository billerRepository, GatewayRepository gatewayRepository, GatewayDailyUsageService gatewayDailyUsageService, PaymentTransactionRepository paymentTransactionRepository, PaymentSplitterService paymentSplitterService, CommissionCalculator commissionCalculator) {
        this.routingService = routingService;
        this.billerRepository = billerRepository;
        this.gatewayRepository = gatewayRepository;
        this.gatewayDailyUsageService = gatewayDailyUsageService;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentSplitterService = paymentSplitterService;
        this.commissionCalculator = commissionCalculator;
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
        paymentTransaction.setCreatedAt(Instant.now());
        paymentTransaction.setUpdatedAt(Instant.now());
        paymentTransactionRepository.saveAndFlush(paymentTransaction);
        return paymentTransaction.getReferenceNumber();
    }

    @Override
    public ProcessSplitPaymentResponse processSplitPayment(ProcessPaymentRequest request) {

        Optional<Biller> billerOptional =
                billerRepository.findByCode(request.getBillerId());

        if (billerOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Biller not found with code: " + request.getBillerId()
            );
        }

        Biller biller = billerOptional.get();

        SplitPaymentRequest splitPaymentRequest =
                new SplitPaymentRequest(
                        request.getBillerId(),
                        request.getAmount(),
                        request.getUrgency()
                );

        SplitPaymentResponse splitPaymentResponse =
                paymentSplitterService.splitPayment(splitPaymentRequest);

        if (Boolean.FALSE.equals(splitPaymentResponse.getQuotaAvailable())) {
            throw new BusinessValidationException(
                    "Selected gateway does not have enough remaining quota"
            );
        }

        Optional<Gateway> gatewayOptional =
                gatewayRepository.findByName(
                        splitPaymentResponse.getSelectedGateway()
                );
        if (gatewayOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Gateway not found with name: " + splitPaymentResponse.getSelectedGateway()
            );
        }
        Gateway gateway = gatewayOptional.get();

        String mainReferenceNumber =
                "SPLIT-" + UUID.randomUUID();

        List<SplitPaymentTransactionItem> transactionItems =
                new ArrayList<>();

        BigDecimal totalCommission = BigDecimal.ZERO;

        for (BigDecimal splitAmount : splitPaymentResponse.getSplits()) {

            BigDecimal splitCommission =
                    commissionCalculator.calculateCommission(
                            splitAmount,
                            gateway.getFixedCommission(),
                            gateway.getPercentageCommission()
                    );

            String transactionReference =
                    "TXN-" + UUID.randomUUID();

            PaymentTransaction paymentTransaction =
                    new PaymentTransaction(
                            transactionReference,
                            splitAmount,
                            splitCommission,
                            TransactionStatus.SUCCESS,
                            biller,
                            gateway,
                            LocalDateTime.now()
                    );
            paymentTransaction.setCreatedAt(Instant.now());
            paymentTransaction.setUpdatedAt(Instant.now());

            paymentTransactionRepository.save(paymentTransaction);

            gatewayDailyUsageService.updateUsage(
                    gateway.getId(),
                    LocalDate.now(),
                    gateway,
                    splitAmount
            );

            totalCommission =
                    totalCommission.add(splitCommission);

            transactionItems.add(
                    new SplitPaymentTransactionItem(
                            transactionReference,
                            splitAmount,
                            splitCommission,
                            TransactionStatus.SUCCESS
                    )
            );
        }

        paymentTransactionRepository.flush();

        return new ProcessSplitPaymentResponse(
                mainReferenceNumber,
                gateway.getName(),
                splitPaymentResponse.getRequiresSplitting(),
                splitPaymentResponse.getSplitCount(),
                request.getAmount(),
                totalCommission,
                transactionItems
        );
    }
}
