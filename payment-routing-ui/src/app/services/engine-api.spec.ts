import { TestBed } from '@angular/core/testing';

import { EngineApi } from './engine-api';

describe('EngineApi', () => {
  let service: EngineApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EngineApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
