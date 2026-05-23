import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Login } from './components/login/login';
import { Dashboard } from './components/dashboard/dashboard';
import { Gateways } from './components/gateways/gateways';
import { PaymentRecommendation } from './components/payment-recommendation/payment-recommendation';
import { Transactions } from './components/transactions/transactions';
import { ProcessPayment } from './components/process-payment/process-payment';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'dashboard',
    component: Dashboard,
  },
  {
    path: 'gateways',
    component: Gateways,
  },
  {
    path: 'payment-recommendation',
    component: PaymentRecommendation,
  },
  {
    path: 'process-payment',
    component: ProcessPayment,
  },
  {
    path: 'transactions',
    component: Transactions,
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
