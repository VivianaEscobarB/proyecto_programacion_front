import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccommodationsPageComponent } from './accommodations';

describe('Accommodations', () => {
  let component: AccommodationsPageComponent;
  let fixture: ComponentFixture<AccommodationsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccommodationsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccommodationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
