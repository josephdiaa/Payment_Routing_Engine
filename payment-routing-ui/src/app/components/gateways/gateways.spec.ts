import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Gateways } from './gateways';

describe('Gateways', () => {
  let component: Gateways;
  let fixture: ComponentFixture<Gateways>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Gateways],
    }).compileComponents();

    fixture = TestBed.createComponent(Gateways);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
