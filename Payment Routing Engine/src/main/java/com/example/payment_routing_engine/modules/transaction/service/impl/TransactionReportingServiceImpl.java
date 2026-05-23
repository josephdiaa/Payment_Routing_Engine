package com.example.payment_routing_engine.modules.transaction.service.impl;

import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import com.example.payment_routing_engine.modules.transaction.domain.PaymentTransaction;
import com.example.payment_routing_engine.modules.transaction.dto.responses.DailyTransactionSummaryResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.GatewayBreakdownItem;
import com.example.payment_routing_engine.modules.transaction.repository.PaymentTransactionRepository;
import com.example.payment_routing_engine.modules.transaction.service.TransactionReportingService;
import com.example.payment_routing_engine.modules.usage.service.GatewayDailyUsageService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@Service
public class TransactionReportingServiceImpl implements TransactionReportingService {
    PaymentTransactionRepository paymentTransactionRepository;
    GatewayDailyUsageService gatewayDailyUsageService;

    public TransactionReportingServiceImpl(PaymentTransactionRepository paymentTransactionRepository, GatewayDailyUsageService gatewayDailyUsageService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.gatewayDailyUsageService = gatewayDailyUsageService;
    }

    public DailyTransactionSummaryResponse transactionsReport(String billerId, LocalDate date) {

        List<PaymentTransaction> paymentTransactionList =
                getTransactionsForDay(billerId, date);

        if (paymentTransactionList.isEmpty()) {
            return new DailyTransactionSummaryResponse(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    List.of()
            );
        }

        Map<Gateway, List<PaymentTransaction>> transactionsGroupedByGateway =
                groupTransactionsByGateway(paymentTransactionList);

        List<GatewayBreakdownItem> gatewayBreakdownItems =
                buildGatewayBreakdownItems(transactionsGroupedByGateway, date);

        BigDecimal totalAmount =
                calculateGrandTotalAmount(gatewayBreakdownItems);

        BigDecimal totalCommission =
                calculateGrandTotalCommission(gatewayBreakdownItems);

        return new DailyTransactionSummaryResponse(
                totalAmount,
                totalCommission,
                gatewayBreakdownItems
        );
    }

    private List<PaymentTransaction> getTransactionsForDay(String billerId, LocalDate date) {

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        return paymentTransactionRepository.findByBillerCodeAndProcessedAtBetween(
                billerId,
                dayStart,
                dayEnd
        );
    }

    private Map<Gateway, List<PaymentTransaction>> groupTransactionsByGateway(
            List<PaymentTransaction> paymentTransactionList
    ) {

        Map<Gateway, List<PaymentTransaction>> paymentTransactionsGroupedByGateway =
                new HashMap<>();

        for (PaymentTransaction paymentTransaction : paymentTransactionList) {

            Gateway gateway = paymentTransaction.getGateway();

            if (!paymentTransactionsGroupedByGateway.containsKey(gateway)) {
                paymentTransactionsGroupedByGateway.put(gateway, new ArrayList<>());
            }

            paymentTransactionsGroupedByGateway.get(gateway).add(paymentTransaction);
        }

        return paymentTransactionsGroupedByGateway;
    }

    private List<GatewayBreakdownItem> buildGatewayBreakdownItems(
            Map<Gateway, List<PaymentTransaction>> transactionsGroupedByGateway,
            LocalDate date
    ) {

        List<GatewayBreakdownItem> gatewayBreakdownItems = new ArrayList<>();

        for (Map.Entry<Gateway, List<PaymentTransaction>> entry
                : transactionsGroupedByGateway.entrySet()) {

            Gateway gateway = entry.getKey();

            List<PaymentTransaction> gatewayTransactions = entry.getValue();

            GatewayBreakdownItem item =
                    buildGatewayBreakdownItem(gateway, gatewayTransactions, date);

            gatewayBreakdownItems.add(item);
        }

        return gatewayBreakdownItems;
    }

    private GatewayBreakdownItem buildGatewayBreakdownItem(
            Gateway gateway,
            List<PaymentTransaction> gatewayTransactions,
            LocalDate date
    ) {

        BigDecimal totalAmount =
                calculateTotalAmount(gatewayTransactions);

        BigDecimal totalCommission =
                calculateTotalCommission(gatewayTransactions);

        BigDecimal remainingQuota =
                gatewayDailyUsageService.getRemainingQuota(
                        gateway.getId(),
                        date,
                        gateway.getDailyLimit()
                );

        BigDecimal usedQuota =
                gateway.getDailyLimit().subtract(remainingQuota);

        GatewayBreakdownItem item = new GatewayBreakdownItem();

        item.setGatewayId(gateway.getId());
        item.setGatewayName(gateway.getName());
        item.setTransactionCount(gatewayTransactions.size());
        item.setTotalAmount(totalAmount);
        item.setTotalCommission(totalCommission);
        item.setUsedQuota(usedQuota);
        item.setRemainingQuota(remainingQuota);

        return item;
    }

    private BigDecimal calculateTotalAmount(
            List<PaymentTransaction> gatewayTransactions
    ) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PaymentTransaction transaction : gatewayTransactions) {
            totalAmount = totalAmount.add(transaction.getAmount());
        }

        return totalAmount;
    }

    private BigDecimal calculateTotalCommission(
            List<PaymentTransaction> gatewayTransactions
    ) {

        BigDecimal totalCommission = BigDecimal.ZERO;

        for (PaymentTransaction transaction : gatewayTransactions) {
            totalCommission = totalCommission.add(transaction.getCommissionAmount());
        }

        return totalCommission;
    }

    private BigDecimal calculateGrandTotalAmount(
            List<GatewayBreakdownItem> gatewayBreakdownItems
    ) {

        BigDecimal grandTotalAmount = BigDecimal.ZERO;

        for (GatewayBreakdownItem item : gatewayBreakdownItems) {
            grandTotalAmount = grandTotalAmount.add(item.getTotalAmount());
        }

        return grandTotalAmount;
    }

    private BigDecimal calculateGrandTotalCommission(
            List<GatewayBreakdownItem> gatewayBreakdownItems
    ) {

        BigDecimal grandTotalCommission = BigDecimal.ZERO;

        for (GatewayBreakdownItem item : gatewayBreakdownItems) {
            grandTotalCommission = grandTotalCommission.add(item.getTotalCommission());
        }

        return grandTotalCommission;
    }

}
