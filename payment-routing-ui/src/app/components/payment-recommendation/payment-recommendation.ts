import { ChangeDetectorRef, Component } from '@angular/core';

import {
  EngineApiService,
  RecommendationResponse,
  SplitPaymentResponse,
  ProcessSplitPaymentResponse,
} from '../../services/engine-api';

@Component({
  selector: 'app-payment-recommendation',
  standalone: false,
  templateUrl: './payment-recommendation.html',
  styleUrl: './payment-recommendation.css',
})
export class PaymentRecommendation {
  billerId = '';
  amount: number | null = null;

  urgency: 'CAN_WAIT' | 'INSTANT' = 'CAN_WAIT';

  recommendationLoading = false;
  splitLoading = false;
  processPaymentLoading = false;
  processSplitLoading = false;

  recommendationError = '';
  splitError = '';
  processError = '';

  successMessage = '';

  recommendationResult: RecommendationResponse | null = null;
  splitResult: SplitPaymentResponse | null = null;

  processPaymentReference = '';
  processSplitResult: ProcessSplitPaymentResponse | null = null;

  constructor(
    private engineApiService: EngineApiService,
    private cdr: ChangeDetectorRef,
  ) {}

  recommendGateway(): void {
    this.clearMessages();
    this.recommendationResult = null;
    this.processPaymentReference = '';

    if (!this.isFormValid()) {
      this.recommendationError = 'Please enter biller ID, amount, and urgency';
      this.cdr.detectChanges();
      return;
    }

    this.recommendationLoading = true;
    this.cdr.detectChanges();

    this.engineApiService
      .recommendGateway({
        billerId: this.billerId.trim(),
        amount: Number(this.amount),
        urgency: this.urgency,
      })
      .subscribe({
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
            'Failed to get gateway recommendation',
          );

          this.recommendationLoading = false;

          this.cdr.detectChanges();
        },
        complete: () => {
          this.recommendationLoading = false;
          this.cdr.detectChanges();
        },
      });
  }

  splitPayment(): void {
    this.clearMessages();
    this.splitResult = null;
    this.processSplitResult = null;

    if (!this.isFormValid()) {
      this.splitError = 'Please enter biller ID, amount, and urgency';
      this.cdr.detectChanges();
      return;
    }

    this.splitLoading = true;
    this.cdr.detectChanges();

    this.engineApiService
      .splitPayment({
        billerId: this.billerId.trim(),
        amount: Number(this.amount),
        urgency: this.urgency,
      })
      .subscribe({
        next: (response) => {
          console.log('Split payment response:', response);

          this.splitResult = response.data;
          this.splitLoading = false;

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.log('Split payment error:', error);

          this.splitError = this.extractErrorMessage(error, 'Failed to calculate split payment');

          this.splitLoading = false;

          this.cdr.detectChanges();
        },
        complete: () => {
          this.splitLoading = false;
          this.cdr.detectChanges();
        },
      });
  }

  processRecommendedPayment(): void {
    this.clearMessages();
    this.processPaymentReference = '';

    if (!this.isFormValid()) {
      this.processError = 'Please enter biller ID, amount, and urgency';
      this.cdr.detectChanges();
      return;
    }

    this.processPaymentLoading = true;
    this.cdr.detectChanges();

    this.engineApiService
      .processPayment({
        billerId: this.billerId.trim(),
        amount: Number(this.amount),
        urgency: this.urgency,
      })
      .subscribe({
        next: (response) => {
          console.log('Process payment response:', response);

          this.processPaymentReference = response.data;
          this.successMessage = 'Payment processed successfully';

          this.processPaymentLoading = false;

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.log('Process payment error:', error);

          this.processError = this.extractErrorMessage(error, 'Failed to process payment');

          this.processPaymentLoading = false;

          this.cdr.detectChanges();
        },
        complete: () => {
          this.processPaymentLoading = false;
          this.cdr.detectChanges();
        },
      });
  }

  processSplitPayment(): void {
    this.clearMessages();
    this.processSplitResult = null;

    if (!this.isFormValid()) {
      this.processError = 'Please enter biller ID, amount, and urgency';
      this.cdr.detectChanges();
      return;
    }

    this.processSplitLoading = true;
    this.cdr.detectChanges();

    this.engineApiService
      .processSplitPayment({
        billerId: this.billerId.trim(),
        amount: Number(this.amount),
        urgency: this.urgency,
      })
      .subscribe({
        next: (response) => {
          console.log('Process split payment response:', response);

          this.processSplitResult = response.data;
          this.successMessage = 'Split payment processed successfully';

          this.processSplitLoading = false;

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.log('Process split payment error:', error);

          this.processError = this.extractErrorMessage(error, 'Failed to process split payment');

          this.processSplitLoading = false;

          this.cdr.detectChanges();
        },
        complete: () => {
          this.processSplitLoading = false;
          this.cdr.detectChanges();
        },
      });
  }

  cancelRecommendation(): void {
    this.recommendationResult = null;
    this.processPaymentReference = '';
    this.clearMessages();
    this.cdr.detectChanges();
  }

  cancelSplit(): void {
    this.splitResult = null;
    this.processSplitResult = null;
    this.clearMessages();
    this.cdr.detectChanges();
  }

  private isFormValid(): boolean {
    return (
      !!this.billerId &&
      this.billerId.trim().length > 0 &&
      this.amount !== null &&
      Number(this.amount) > 0 &&
      !!this.urgency
    );
  }

  private clearMessages(): void {
    this.recommendationError = '';
    this.splitError = '';
    this.processError = '';
    this.successMessage = '';
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
