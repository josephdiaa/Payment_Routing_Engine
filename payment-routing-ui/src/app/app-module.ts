import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';

import { Login } from './components/login/login';
import { Dashboard } from './components/dashboard/dashboard';
import { Gateways } from './components/gateways/gateways';
import { PaymentRecommendation } from './components/payment-recommendation/payment-recommendation';
import { Transactions } from './components/transactions/transactions';

import { authInterceptor } from './interceptors/auth-interceptor';
import { ProcessPayment } from './components/process-payment/process-payment';

@NgModule({
  declarations: [
    App,
    Login,
    Dashboard,
    Gateways,
    PaymentRecommendation,
    Transactions,
    ProcessPayment
  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideHttpClient(withInterceptors([authInterceptor]))],
  bootstrap: [App],
})
export class AppModule {}
