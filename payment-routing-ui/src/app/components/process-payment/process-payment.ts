import { ChangeDetectorRef, Component } from '@angular/core';

import { EngineApiService } from '../../services/engine-api';

@Component({
  selector: 'app-process-payment',
  standalone: false,
  templateUrl: './process-payment.html',
  styleUrl: './process-payment.css',
})
export class ProcessPayment {
  billerId = '';
  amount: number | null = null;
  urgency: 'CAN_WAIT' | 'INSTANT' = 'CAN_WAIT';

  loading = false;

  errorMessage = '';

  successMessage = '';

  processResult = '';

  constructor(
    private engineApiService: EngineApiService,
    private cdr: ChangeDetectorRef,
  ) {}

  processPayment(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.processResult = '';

    if (!this.billerId || this.billerId.trim().length === 0) {
      this.errorMessage = 'Please enter biller ID';
      this.cdr.detectChanges();
      return;
    }

    if (this.amount === null || Number(this.amount) <= 0) {
      this.errorMessage = 'Please enter amount greater than 0';
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
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

          this.processResult = response.data;
          this.successMessage = response.message || 'Payment processed successfully';

          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.log('Process payment error:', error);

          this.errorMessage =
            error?.error?.message || error?.message || 'Failed to process payment';

          this.loading = false;
          this.cdr.detectChanges();
        },
        complete: () => {
          this.loading = false;
          this.cdr.detectChanges();
        },
      });
  }

  resetForm(): void {
    this.billerId = '';
    this.amount = null;
    this.urgency = 'CAN_WAIT';
    this.errorMessage = '';
    this.successMessage = '';
    this.processResult = '';

    this.cdr.detectChanges();
  }

  formatMoney(value: number | null): string {
    if (value === null || value === undefined) {
      return 'EGP 0';
    }

    return `EGP ${Number(value).toLocaleString()}`;
  }
}
