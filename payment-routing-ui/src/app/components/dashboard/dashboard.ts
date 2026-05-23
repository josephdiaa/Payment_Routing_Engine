import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../services/auth';

import {
  DashboardService,
  GatewayResponse,
  GatewayBreakdownItem,
  DailyTransactionSummaryResponse
} from '../../services/dashboard';

interface MetricCard {
  title: string;
  value: string;
  change: string;
  icon: string;
  trend: 'up' | 'down' | 'neutral';
}

interface GatewayCard {
  name: string;
  status: 'Active' | 'Warning' | 'Inactive';
  successRate: string;
  dailyUsage: string;
  quota: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  userName = 'Admin';

  billerId = 'BILLER001';

  selectedDate = new Date().toISOString().split('T')[0];

  loading = false;

  errorMessage = '';

  metrics: MetricCard[] = [];

  gateways: GatewayCard[] = [];

  breakdown: GatewayBreakdownItem[] = [];

  constructor(
    private authService: AuthService,
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      gatewaysResponse: this.dashboardService.getGateways(),
      summaryResponse: this.dashboardService.getDailyTransactionSummary(
        this.billerId,
        this.selectedDate
      )
    }).subscribe({
      next: ({ gatewaysResponse, summaryResponse }) => {

        const backendGateways = gatewaysResponse.data || [];

        const summary = summaryResponse.data;

        this.breakdown = summary?.breakdown || [];

        this.gateways = this.buildGatewayCards(
          backendGateways,
          this.breakdown
        );

        this.metrics = this.buildMetrics(
          summary,
          backendGateways
        );

        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load dashboard data';
      }
    });
  }

  buildMetrics(
    summary: DailyTransactionSummaryResponse,
    backendGateways: GatewayResponse[]
  ): MetricCard[] {

    const totalTransactions = this.getTotalTransactions(
      summary?.breakdown || []
    );

    const activeGateways = backendGateways.filter(
      gateway => gateway.active
    ).length;

    return [
      {
        title: 'Total Transactions',
        value: totalTransactions.toString(),
        change: 'Today selected date',
        icon: '↗',
        trend: 'neutral'
      },
      {
        title: 'Total Amount',
        value: this.formatMoney(summary?.totalProcessedAmount || 0),
        change: 'Processed amount',
        icon: '💳',
        trend: 'up'
      },
      {
        title: 'Total Commission',
        value: this.formatMoney(summary?.totalCommissionCharged || 0),
        change: 'Commission charged',
        icon: '٪',
        trend: 'up'
      },
      {
        title: 'Active Gateways',
        value: activeGateways.toString(),
        change: 'Currently enabled',
        icon: '🔀',
        trend: 'neutral'
      }
    ];
  }

  buildGatewayCards(
    backendGateways: GatewayResponse[],
    breakdown: GatewayBreakdownItem[]
  ): GatewayCard[] {

    return backendGateways.map(gateway => {

      const gatewayBreakdown = breakdown.find(
        item => item.gatewayId === gateway.id
      );

      const usedQuota = gatewayBreakdown?.usedQuota || 0;

      const dailyLimit =
        gatewayBreakdown?.dailyLimit ||
        gateway.dailyLimit ||
        0;

      const usagePercentage =
        dailyLimit > 0
          ? Math.round((usedQuota / dailyLimit) * 100)
          : 0;

      return {
        name: gateway.name,
        status: this.getGatewayStatus(
          gateway.active,
          usagePercentage
        ),
        successRate: 'N/A',
        dailyUsage: `${usagePercentage}%`,
        quota: `${this.formatMoney(usedQuota)} / ${this.formatMoney(dailyLimit)}`
      };
    });
  }

  getGatewayStatus(
    active: boolean,
    usagePercentage: number
  ): 'Active' | 'Warning' | 'Inactive' {

    if (!active) {
      return 'Inactive';
    }

    if (usagePercentage >= 90) {
      return 'Warning';
    }

    return 'Active';
  }

  getTotalTransactions(
    breakdown: GatewayBreakdownItem[]
  ): number {

    return breakdown.reduce(
      (total, item) => total + item.transactionCount,
      0
    );
  }

  formatMoney(value: number): string {

    if (!value) {
      return 'EGP 0';
    }

    return `EGP ${value.toLocaleString()}`;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}