import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}
export interface ProcessPaymentRequest {
  billerId: string;
  amount: number;
  urgency: string;
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
  maxTransactionAmount: number | null;
  active: boolean;
}

export interface CreateGatewayRequest {
  name: string;
  fixedCommission: number;
  percentageCommission: number;
  dailyLimit: number;
  processingTime: string;
  availabilityStart?: string | null;
  availabilityEnd?: string | null;
  availableDays: string;
  minTransactionAmount: number;
  maxTransactionAmount?: number | null;
  active: boolean;
}

export interface UpdateGatewayRequest {
  name: string;
  fixedCommission: number;
  percentageCommission: number;
  dailyLimit: number;
  processingTime: string;
  availabilityStart?: string | null;
  availabilityEnd?: string | null;
  availableDays: string;
  minTransactionAmount: number;
  maxTransactionAmount?: number | null;
  active: boolean;
}

export interface UpdateGatewayStatusRequest {
  active: boolean;
}

export interface RecommendationRequest {
  billerId: string;
  amount: number;
  urgency: string;
}

export interface GatewayRecommendationItem {
  id: string;
  name: string;
  estimatedCommission: number;
  processingTime: string;
}

export interface RecommendationResponse {
  recommendedGateway: GatewayRecommendationItem;
  alternatives: GatewayRecommendationItem[];
}

export interface SplitPaymentRequest {
  billerId: string;
  amount: number;
  urgency: string;
}

export interface SplitPaymentResponse {
  selectedGateway: string;
  requiresSplitting: boolean;
  splits: number[];
  splitCount: number;
  totalCommission: number;
  quotaAvailable: boolean;
}

export interface ProcessPaymentRequest {
  billerId: string;
  amount: number;
  urgency: string;
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
  providedIn: 'root',
})
export class EngineApiService {
  private readonly baseUrl = 'http://localhost:8090/api';

  constructor(private http: HttpClient) {}

  getGateways(): Observable<ApiResponse<GatewayResponse[]>> {
    return this.http.get<ApiResponse<GatewayResponse[]>>(`${this.baseUrl}/gateways`);
  }

  createGateway(request: CreateGatewayRequest): Observable<ApiResponse<GatewayResponse>> {
    return this.http.post<ApiResponse<GatewayResponse>>(`${this.baseUrl}/gateways`, request);
  }

  updateGateway(
    gatewayId: string,
    request: UpdateGatewayRequest,
  ): Observable<ApiResponse<GatewayResponse>> {
    return this.http.put<ApiResponse<GatewayResponse>>(
      `${this.baseUrl}/gateways/${gatewayId}`,
      request,
    );
  }

  updateGatewayStatus(
    gatewayId: string,
    request: UpdateGatewayStatusRequest,
  ): Observable<ApiResponse<GatewayResponse>> {
    return this.http.patch<ApiResponse<GatewayResponse>>(
      `${this.baseUrl}/gateways/${gatewayId}/status`,
      request,
    );
  }

  recommendGateway(
    request: RecommendationRequest,
  ): Observable<ApiResponse<RecommendationResponse>> {
    return this.http.post<ApiResponse<RecommendationResponse>>(
      `${this.baseUrl}/payments/recommend`,
      request,
    );
  }

  splitPayment(request: SplitPaymentRequest): Observable<ApiResponse<SplitPaymentResponse>> {
    return this.http.post<ApiResponse<SplitPaymentResponse>>(
      `${this.baseUrl}/payments/split`,
      request,
    );
  }

  processPayment(request: ProcessPaymentRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.baseUrl}/payments/process`, request);
  }

  getDailyTransactionSummary(
    billerId: string,
    date: string,
  ): Observable<ApiResponse<DailyTransactionSummaryResponse>> {
    const params = new HttpParams().set('date', date);

    return this.http.get<ApiResponse<DailyTransactionSummaryResponse>>(
      `${this.baseUrl}/billers/${billerId}/transactions`,
      { params },
    );
  }

}
