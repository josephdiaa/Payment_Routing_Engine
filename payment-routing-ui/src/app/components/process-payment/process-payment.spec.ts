import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessPayment } from './process-payment';

describe('ProcessPayment', () => {
  let component: ProcessPayment;
  let fixture: ComponentFixture<ProcessPayment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProcessPayment],
    }).compileComponents();

    fixture = TestBed.createComponent(ProcessPayment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
