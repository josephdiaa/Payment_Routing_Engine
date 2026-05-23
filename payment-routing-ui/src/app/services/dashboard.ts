import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}

export interface GatewayResponse {
  id: string;
  name: string;
  fixedCommission: number;
  percentageCommission: number;
  dailyLimit: number;
  processingTime: string;
  availabilityStart: any;
  availabilityEnd: any;
  availableDays: string;
  minTransactionAmount: number;
  maxTransactionAmount: number;
  active: boolean;
}

export interface DailyTransactionSummaryResponse {
  totalProcessedAmount: number;
  totalCommissionCharged: number;
  breakdown: GatewayBreakdownItem[];
}

export interface GatewayBreakdownItem {
  gatewayId: string;
  gatewayName: string;
  transactionCount: number;
  totalAmount: number;
  totalCommission: number;
  dailyLimit: number;
  usedQuota: number;
  remainingQuota: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly baseUrl = 'http://localhost:8090/api';

  constructor(private http: HttpClient) {}

  getGateways(): Observable<ApiResponse<GatewayResponse[]>> {
    return this.http.get<ApiResponse<GatewayResponse[]>>(
      `${this.baseUrl}/gateways`
    );
  }

  getDailyTransactionSummary(
    billerId: string,
    date: string
  ): Observable<ApiResponse<DailyTransactionSummaryResponse>> {

    const params = new HttpParams().set('date', date);

    return this.http.get<ApiResponse<DailyTransactionSummaryResponse>>(
      `${this.baseUrl}/billers/${billerId}/transactions`,
      { params }
    );
  }
}