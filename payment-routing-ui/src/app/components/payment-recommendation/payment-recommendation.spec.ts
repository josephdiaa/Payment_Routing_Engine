import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentRecommendation } from './payment-recommendation';

describe('PaymentRecommendation', () => {
  let component: PaymentRecommendation;
  let fixture: ComponentFixture<PaymentRecommendation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentRecommendation],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentRecommendation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
