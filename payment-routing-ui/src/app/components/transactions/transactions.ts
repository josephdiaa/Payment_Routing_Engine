import { ChangeDetectorRef, Component } from '@angular/core';
import {
  EngineApiService,
  DailyTransactionSummaryResponse,
  GatewayBreakdownItem,
} from '../../services/engine-api';

@Component({
  selector: 'app-transactions',
  standalone: false,
  templateUrl: './transactions.html',
  styleUrl: './transactions.css',
})
export class Transactions {
  billerId = 'WE';

  selectedDate = new Date().toISOString().split('T')[0];

  loading = false;

  errorMessage = '';

  summary: DailyTransactionSummaryResponse | null = null;

  breakdown: GatewayBreakdownItem[] = [];

  constructor(
    private engineApiService: EngineApiService,
    private cdr: ChangeDetectorRef,
  ) {}

  loadTransactions(): void {
    this.errorMessage = '';
    this.summary = null;
    this.breakdown = [];

    if (!this.billerId || this.billerId.trim().length === 0) {
      this.errorMessage = 'Please enter biller ID';
      this.cdr.detectChanges();
      return;
    }

    if (!this.selectedDate) {
      this.errorMessage = 'Please select date';
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    this.engineApiService
      .getDailyTransactionSummary(this.billerId.trim(), this.selectedDate)
      .subscribe({
        next: (response) => {
          console.log('Transaction summary response:', response);

          this.summary = response.data;
          this.breakdown = response.data?.breakdown || [];

          this.loading = false;
          this.cdr.detectChanges();
        },

        error: (error) => {
          console.log('Transaction summary error:', error);

          this.errorMessage =
            error?.error?.message || error?.message || 'Failed to load transaction summary';

          this.loading = false;
          this.cdr.detectChanges();
        },

        complete: () => {
          this.loading = false;
          this.cdr.detectChanges();
        },
      });
  }

  getTotalTransactions(): number {
    return this.breakdown.reduce((total, item) => total + item.transactionCount, 0);
  }

  formatMoney(value: number): string {
    if (value === null || value === undefined) {
      return 'EGP 0';
    }

    return `EGP ${Number(value).toLocaleString()}`;
  }
}
