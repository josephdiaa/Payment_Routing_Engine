import { ChangeDetectorRef, Component } from '@angular/core';

import {
  EngineApiService,
  RecommendationResponse,
  SplitPaymentResponse
} from '../../services/engine-api';

@Component({
  selector: 'app-payment-recommendation',
  standalone: false,
  templateUrl: './payment-recommendation.html',
  styleUrl: './payment-recommendation.css'
})
export class PaymentRecommendation {

  billerId = '';
  amount: number | null = null;

  // Backend accepts only these values
  urgency: 'CAN_WAIT' | 'INSTANT' = 'CAN_WAIT';

  recommendationLoading = false;
  splitLoading = false;

  recommendationError = '';
  splitError = '';

  recommendationResult: RecommendationResponse | null = null;
  splitResult: SplitPaymentResponse | null = null;

  constructor(
    private engineApiService: EngineApiService,
    private cdr: ChangeDetectorRef
  ) {}

  recommendGateway(): void {
    this.recommendationError = '';
    this.recommendationResult = null;

    if (!this.isFormValid()) {
      this.recommendationError = 'Please enter biller ID, amount, and urgency';
      this.cdr.detectChanges();
      return;
    }

    this.recommendationLoading = true;
    this.cdr.detectChanges();

    this.engineApiService.recommendGateway({
      billerId: this.billerId.trim(),
      amount: Number(this.amount),
      urgency: this.urgency
    }).subscribe({
      next: (response) => {
        console.log('Recommendation response:', response);

        this.recommendationResult = response.data;
        this.recommendationLoading = false;

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Recommendation error:', error);

        this.recommendationError = this.extractErrorMessage(
          error,
          'Failed to get gateway recommendation'
        );

        this.recommendationLoading = false;

        this.cdr.detectChanges();
      },
      complete: () => {
        this.recommendationLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  splitPayment(): void {
    this.splitError = '';
    this.splitResult = null;

    if (!this.isFormValid()) {
      this.splitError = 'Please enter biller ID, amount, and urgency';
      this.cdr.detectChanges();
      return;
    }

    this.splitLoading = true;
    this.cdr.detectChanges();

    this.engineApiService.splitPayment({
      billerId: this.billerId.trim(),
      amount: Number(this.amount),
      urgency: this.urgency
    }).subscribe({
      next: (response) => {
        console.log('Split payment response:', response);

        this.splitResult = response.data;
        this.splitLoading = false;

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Split payment error:', error);

        this.splitError = this.extractErrorMessage(
          error,
          'Failed to calculate split payment'
        );

        this.splitLoading = false;

        this.cdr.detectChanges();
      },
      complete: () => {
        this.splitLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private isFormValid(): boolean {
    return !!this.billerId &&
      this.billerId.trim().length > 0 &&
      this.amount !== null &&
      Number(this.amount) > 0 &&
      !!this.urgency;
  }

  private extractErrorMessage(error: any, defaultMessage: string): string {
    if (error?.error?.message) {
      return error.error.message;
    }

    if (error?.message) {
      return error.message;
    }

    return defaultMessage;
  }

  formatMoney(value: number): string {
    if (value === null || value === undefined) {
      return 'EGP 0';
    }

    return `EGP ${Number(value).toLocaleString()}`;
  }
}
