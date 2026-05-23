import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth';

import {
  EngineApiService,
  GatewayResponse,
  CreateGatewayRequest,
  UpdateGatewayRequest,
} from '../../services/engine-api';

@Component({
  selector: 'app-gateways',
  standalone: false,
  templateUrl: './gateways.html',
  styleUrl: './gateways.css',
})
export class Gateways implements OnInit {
  gateways: GatewayResponse[] = [];

  loading = false;

  errorMessage = '';

  successMessage = '';

  updatingGatewayId = '';

  showCreateForm = false;

  savingGateway = false;

  editingGatewayId: string | null = null;

  availableDaysDropdownOpen = false;

  availableDaysOptions = [
    { label: 'Saturday', value: 'SAT' },
    { label: 'Sunday', value: 'SUN' },
    { label: 'Monday', value: 'MON' },
    { label: 'Tuesday', value: 'TUE' },
    { label: 'Wednesday', value: 'WED' },
    { label: 'Thursday', value: 'THU' },
    { label: 'Friday', value: 'FRI' },
  ];

  selectedAvailableDays: string[] = ['SAT', 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI'];

  processingTimeOptions = ['Instant', '2 hours', '24 hours'];

  newGateway = {
    name: '',
    fixedCommission: 0,
    percentageCommission: 0,
    dailyLimit: 0,
    processingTime: 'Instant',
    availabilityStart: '',
    availabilityEnd: '',
    availableDays: 'SAT,SUN,MON,TUE,WED,THU,FRI',
    minTransactionAmount: 1,
    maxTransactionAmount: null as number | null,
    active: true,
  };

  constructor(
    private engineApiService: EngineApiService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadGateways();
  }

  loadGateways(): void {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.cdr.detectChanges();

    this.engineApiService.getGateways().subscribe({
      next: (response) => {
        this.gateways = response.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Failed to load gateways:', error);

        this.errorMessage = 'Failed to load gateways';
        this.loading = false;

        this.cdr.detectChanges();
      },
    });
  }

  openCreateForm(): void {
    this.editingGatewayId = null;
    this.resetGatewayForm();
    this.showCreateForm = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();
  }

  startEditGateway(gateway: GatewayResponse): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.editingGatewayId = gateway.id;

    this.selectedAvailableDays = this.parseAvailableDays(gateway.availableDays);

    this.newGateway = {
      name: gateway.name,
      fixedCommission: gateway.fixedCommission,
      percentageCommission: gateway.percentageCommission,
      dailyLimit: gateway.dailyLimit,
      processingTime: gateway.processingTime,
      availabilityStart: this.convertBackendTimeToInputFormat(gateway.availabilityStart),
      availabilityEnd: this.convertBackendTimeToInputFormat(gateway.availabilityEnd),
      availableDays: gateway.availableDays,
      minTransactionAmount: gateway.minTransactionAmount,
      maxTransactionAmount: gateway.maxTransactionAmount,
      active: gateway.active,
    };

    this.showCreateForm = true;
    this.availableDaysDropdownOpen = false;

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });

    this.cdr.detectChanges();
  }

  cancelGatewayForm(): void {
    this.editingGatewayId = null;
    this.showCreateForm = false;
    this.resetGatewayForm();
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();
  }

  submitGatewayForm(): void {
    if (this.editingGatewayId) {
      this.updateGateway();
    } else {
      this.createGateway();
    }
  }

  createGateway(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.isGatewayFormValid()) {
      return;
    }

    this.savingGateway = true;
    this.cdr.detectChanges();

    const request: CreateGatewayRequest = this.buildGatewayRequest();

    this.engineApiService.createGateway(request).subscribe({
      next: (response) => {
        const createdGateway = response.data;

        this.gateways = [createdGateway, ...this.gateways];

        this.successMessage = 'Gateway created successfully';
        this.errorMessage = '';

        this.savingGateway = false;
        this.showCreateForm = false;

        this.resetGatewayForm();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Failed to create gateway:', error);

        this.errorMessage = this.extractErrorMessage(error, 'Failed to create gateway');

        this.successMessage = '';
        this.savingGateway = false;

        this.cdr.detectChanges();
      },
      complete: () => {
        this.savingGateway = false;
        this.cdr.detectChanges();
      },
    });
  }

  updateGateway(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.editingGatewayId) {
      this.errorMessage = 'No gateway selected for update';
      this.cdr.detectChanges();
      return;
    }

    if (!this.isGatewayFormValid()) {
      return;
    }

    this.savingGateway = true;
    this.cdr.detectChanges();

    const request: UpdateGatewayRequest = this.buildGatewayRequest();

    this.engineApiService.updateGateway(this.editingGatewayId, request).subscribe({
      next: (response) => {
        const updatedGateway = response.data;

        this.gateways = this.gateways.map((item) => {
          if (item.id === updatedGateway.id) {
            return updatedGateway;
          }

          return item;
        });

        this.successMessage = 'Gateway updated successfully';
        this.errorMessage = '';

        this.savingGateway = false;
        this.showCreateForm = false;
        this.editingGatewayId = null;

        this.resetGatewayForm();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Failed to update gateway:', error);

        this.errorMessage = this.extractErrorMessage(error, 'Failed to update gateway');

        this.successMessage = '';
        this.savingGateway = false;

        this.cdr.detectChanges();
      },
      complete: () => {
        this.savingGateway = false;
        this.cdr.detectChanges();
      },
    });
  }

  toggleGatewayStatus(gateway: GatewayResponse): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.updatingGatewayId = gateway.id;

    const newStatus = !gateway.active;

    this.cdr.detectChanges();

    this.engineApiService
      .updateGatewayStatus(gateway.id, {
        active: newStatus,
      })
      .subscribe({
        next: (response) => {
          const updatedGateway = response.data;

          this.gateways = this.gateways.map((item) => {
            if (item.id === updatedGateway.id) {
              return updatedGateway;
            }

            return item;
          });

          this.successMessage = newStatus
            ? 'Gateway activated successfully'
            : 'Gateway deactivated successfully';

          this.updatingGatewayId = '';

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.log('Failed to update gateway status:', error);

          this.errorMessage = 'Failed to update gateway status';
          this.updatingGatewayId = '';

          this.cdr.detectChanges();
        },
      });
  }

  private buildGatewayRequest(): CreateGatewayRequest {
    return {
      name: this.newGateway.name.trim(),

      fixedCommission: Number(this.newGateway.fixedCommission),

      percentageCommission: Number(this.newGateway.percentageCommission),

      dailyLimit: Number(this.newGateway.dailyLimit),

      processingTime: this.newGateway.processingTime,

      availabilityStart: this.convertOptionalTimeToBackendFormat(this.newGateway.availabilityStart),

      availabilityEnd: this.convertOptionalTimeToBackendFormat(this.newGateway.availabilityEnd),

      availableDays: this.getAvailableDaysValue(),

      minTransactionAmount: Number(this.newGateway.minTransactionAmount),

      maxTransactionAmount:
        this.newGateway.maxTransactionAmount !== null &&
        this.newGateway.maxTransactionAmount !== undefined &&
        Number(this.newGateway.maxTransactionAmount) > 0
          ? Number(this.newGateway.maxTransactionAmount)
          : null,

      active: this.newGateway.active,
    };
  }

  private isGatewayFormValid(): boolean {
    if (!this.newGateway.name || this.newGateway.name.trim().length === 0) {
      this.errorMessage = 'Please enter gateway name';
      this.cdr.detectChanges();
      return false;
    }

    if (Number(this.newGateway.fixedCommission) < 0) {
      this.errorMessage = 'Fixed commission cannot be negative';
      this.cdr.detectChanges();
      return false;
    }

    if (Number(this.newGateway.percentageCommission) < 0) {
      this.errorMessage = 'Percentage commission cannot be negative';
      this.cdr.detectChanges();
      return false;
    }

    if (Number(this.newGateway.dailyLimit) <= 0) {
      this.errorMessage = 'Daily limit must be greater than 0';
      this.cdr.detectChanges();
      return false;
    }

    if (Number(this.newGateway.minTransactionAmount) <= 0) {
      this.errorMessage = 'Minimum transaction amount must be greater than 0';
      this.cdr.detectChanges();
      return false;
    }

    if (
      this.newGateway.maxTransactionAmount !== null &&
      this.newGateway.maxTransactionAmount !== undefined &&
      Number(this.newGateway.maxTransactionAmount) > 0 &&
      Number(this.newGateway.maxTransactionAmount) < Number(this.newGateway.minTransactionAmount)
    ) {
      this.errorMessage =
        'Maximum transaction amount cannot be less than minimum transaction amount';
      this.cdr.detectChanges();
      return false;
    }

    if (this.selectedAvailableDays.length === 0) {
      this.errorMessage = 'Please select at least one available day';
      this.cdr.detectChanges();
      return false;
    }

    return true;
  }

  toggleAvailableDaysDropdown(): void {
    this.availableDaysDropdownOpen = !this.availableDaysDropdownOpen;
  }

  toggleAvailableDay(day: string): void {
    const alreadySelected = this.selectedAvailableDays.includes(day);

    if (alreadySelected) {
      this.selectedAvailableDays = this.selectedAvailableDays.filter(
        (selectedDay) => selectedDay !== day,
      );
    } else {
      this.selectedAvailableDays = [...this.selectedAvailableDays, day];
    }

    this.newGateway.availableDays = this.getAvailableDaysValue();

    this.cdr.detectChanges();
  }

  isDaySelected(day: string): boolean {
    return this.selectedAvailableDays.includes(day);
  }

  getSelectedDaysLabel(): string {
    if (this.selectedAvailableDays.length === 0) {
      return 'Select available days';
    }

    if (this.selectedAvailableDays.length === this.availableDaysOptions.length) {
      return 'All Days';
    }

    return this.selectedAvailableDays.join(', ');
  }

  getAvailableDaysValue(): string {
    return this.selectedAvailableDays.join(',');
  }

  selectAllDays(): void {
    this.selectedAvailableDays = this.availableDaysOptions.map((option) => option.value);

    this.newGateway.availableDays = this.getAvailableDaysValue();

    this.cdr.detectChanges();
  }

  clearAllDays(): void {
    this.selectedAvailableDays = [];

    this.newGateway.availableDays = '';

    this.cdr.detectChanges();
  }

  parseAvailableDays(availableDays: string | null): string[] {
    if (!availableDays) {
      return [];
    }

    return availableDays
      .split(',')
      .map((day) => day.trim())
      .filter((day) => day.length > 0);
  }

  convertOptionalTimeToBackendFormat(time: string): string | null {
    if (!time || time.trim() === '') {
      return null;
    }

    if (time.length === 5) {
      return `${time}:00`;
    }

    return time;
  }

  convertBackendTimeToInputFormat(time: any): string {
    if (!time) {
      return '';
    }

    if (typeof time === 'string') {
      return time.length >= 5 ? time.substring(0, 5) : time;
    }

    if (typeof time === 'object' && time.hour !== undefined && time.minute !== undefined) {
      const hour = String(time.hour).padStart(2, '0');
      const minute = String(time.minute).padStart(2, '0');

      return `${hour}:${minute}`;
    }

    return '';
  }

  formatTimeDisplay(time: any): string {
    if (!time) {
      return '-';
    }

    if (typeof time === 'string') {
      return time;
    }

    if (typeof time === 'object' && time.hour !== undefined && time.minute !== undefined) {
      const hour = String(time.hour).padStart(2, '0');
      const minute = String(time.minute).padStart(2, '0');

      return `${hour}:${minute}`;
    }

    return '-';
  }

  resetGatewayForm(): void {
    this.selectedAvailableDays = ['SAT', 'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI'];

    this.newGateway = {
      name: '',
      fixedCommission: 0,
      percentageCommission: 0,
      dailyLimit: 0,
      processingTime: 'Instant',
      availabilityStart: '',
      availabilityEnd: '',
      availableDays: this.getAvailableDaysValue(),
      minTransactionAmount: 1,
      maxTransactionAmount: null,
      active: true,
    };

    this.availableDaysDropdownOpen = false;
  }

  resetCreateGatewayForm(): void {
    this.resetGatewayForm();
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

  formatMoney(value: number | null): string {
    if (value === null || value === undefined) {
      return 'EGP 0';
    }

    return `EGP ${Number(value).toLocaleString()}`;
  }
}
