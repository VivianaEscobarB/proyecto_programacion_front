import { TestBed } from '@angular/core/testing';

import { AccommodationService } from './accommodation';

describe('Accommodation', () => {
  let service: AccommodationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccommodationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
