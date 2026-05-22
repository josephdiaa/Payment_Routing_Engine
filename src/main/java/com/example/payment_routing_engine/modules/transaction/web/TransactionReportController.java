package com.example.payment_routing_engine.modules.transaction.web;

import com.example.payment_routing_engine.common.response.ApiResponse;
import com.example.payment_routing_engine.modules.transaction.dto.responses.DailyTransactionSummaryResponse;
import com.example.payment_routing_engine.modules.transaction.service.TransactionReportingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class TransactionReportController  {
    public TransactionReportingService transactionReportingService;

    public TransactionReportController(TransactionReportingService transactionReportingService) {
        this.transactionReportingService = transactionReportingService;
    }

    @GetMapping("/api/billers/{billerId}/transactions")
    public ResponseEntity<ApiResponse<DailyTransactionSummaryResponse>> getDailySummary(
            @PathVariable String billerId,
            @RequestParam LocalDate date) {

        DailyTransactionSummaryResponse response = transactionReportingService.transactionsReport(billerId, date);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
