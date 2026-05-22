package com.example.payment_routing_engine.modules.transaction.service;

import com.example.payment_routing_engine.modules.transaction.dto.responses.DailyTransactionSummaryResponse;

import java.time.LocalDate;

public interface TransactionReportingService {
    public DailyTransactionSummaryResponse transactionsReport(String billerId, LocalDate date);
}
